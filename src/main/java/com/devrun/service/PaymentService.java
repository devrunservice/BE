package com.devrun.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devrun.dto.PaymentDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.PaymentEntity;
import com.devrun.entity.PointEntity;
import com.devrun.entity.PointHistoryEntity;
import com.devrun.repository.CouponregicodeRepository;
import com.devrun.repository.MemberEntityRepository;
import com.devrun.repository.PaymentRepository;
import com.devrun.repository.PointHistoryRepository;
import com.devrun.repository.PointRepository;
import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureRepository;

@Service
public class PaymentService {
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private PointRepository pointRepository;	
	@Autowired
	private MemberEntityRepository memberEntityRepository;	
	@Autowired
	private CouponregicodeRepository couponregicodeRepository;	
	@Autowired
	private PointHistoryRepository pointHistoryRepository;	
	@Autowired
	private LectureRepository lectureRepository;	
	@Autowired
	private MyLectureService myLectureService;		


	public void savePayment(List<PaymentDTO> paymentDTOList) {
		LocalDateTime dateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm:ss", new Locale("ko"));
		String paymentDate = dateTime.format(formatter);
		
		String couponCode =paymentDTOList.get(0).getCouponCode();
		
		couponregicodeRepository.couponCodeUsed(couponCode);
		
		System.err.println(paymentDTOList);
		
		// 사용자의 포인트 정보를 조회
		//2개이상 구매시 구매자는 이름이 같으니깐, 첫번째 배열에 있는 이름으로 point찾음.
	    int usrno = paymentDTOList.get(0).getUserno();
	    //userno로 처리하기 
	    
	    System.err.println(usrno);
	    PointEntity pointEntity = pointRepository.findByMemberEntity_userNo(usrno);
	    System.err.println(pointEntity);

	    if (pointEntity == null) {
	        throw new IllegalArgumentException("사용자의 포인트 정보가 없습니다.");
	    }

	    // 사용자의 포인트에서 지불할 금액 계산
	    int userPoint = 0;
	    for (PaymentDTO paymentDTO : paymentDTOList) {
	        userPoint += paymentDTO.getMypoint();
	    }

	    int nowPoint = pointEntity.getMypoint();

	    if (nowPoint < userPoint) {
	        // 사용자의 포인트가 부족하여 처리할 수 없는 경우 에러 응답 반환
	        throw new IllegalArgumentException("보유한 포인트보다 사용 할 포인트가 더 많습니다.");
	    }

	    // 사용자의 포인트에서 지불할 금액만큼 차감
	    int updatedPoint = nowPoint - userPoint;
	    pointEntity.setMypoint(updatedPoint);
	    pointRepository.save(pointEntity); 
	    
	    //외래키에 값 넣어주기.
	    //결제 정보 사용자 이름으로 memberEntity에서 찾은후, 밑에 추가해주기. 
	    //외래키가 user_no지만 memberEntity로 정의해서 저렇게 넣어줘야함.
	    MemberEntity memberEntity = memberEntityRepository.findByUserNo(usrno);	

		 // 리스트로 들어오는 모든 강의 정보 먼저 저장시킴
	     // Map 활용
		   Map<String, Lecture> lectureMap = new HashMap<>();				    
		   for (PaymentDTO paymentDTO : paymentDTOList) {
		        String lectureName = paymentDTO.getName();
		        Lecture lecture = lectureRepository.findByLectureName(lectureName);
		        lectureMap.put(lectureName, lecture);
		    }
	    System.err.println(memberEntity);		
		try {		
			List<PaymentEntity> paymentList = new ArrayList<>();					
	        for (PaymentDTO paymentDTO : paymentDTOList) {
	        PaymentEntity paymentEntity = new PaymentEntity();
	        paymentEntity.setName(paymentDTO.getName());
	        paymentEntity.setPaid_amount(paymentDTO.getPaid_amount());
	        paymentEntity.setImp_uid(paymentDTO.getImp_uid());
	        paymentEntity.setMerchant_uid(paymentDTO.getMerchant_uid());
	        paymentEntity.setReceipt_url(paymentDTO.getReceipt_url());
			paymentEntity.setPaymentDate(paymentDate);
			paymentEntity.setStatus("0");					
			
			paymentEntity.setMemberEntity(memberEntity);
			
			// 강의이름을 다시 가져오고 map key value값으로 사용해서 매핑되는 lectureid값 넣어줌
		    String lectureName = paymentDTO.getName();
		    Lecture lecture = lectureMap.get(lectureName);
		    paymentEntity.setLecture(lecture);					
	        paymentList.add(paymentEntity);
	    
	      //구매한 강의를 내 수강 목록 DB에 등록
		  //Mylecture 와 MylectureProgress 등록
	      //이부분도 한개 이상의 강의가 들어왔을 경우 처리해야해서 for문안으로 넣어줌
		    myLectureService.registLecture(memberEntity, lecture);
		    
	       }		    
		    pointRepository.save(pointEntity);			    
			paymentRepository.saveAll(paymentList);
			
			    //포인트 사용했으면 실행
			    if(userPoint > 0) {
			    	
			    	  String productName = "";
					    int productCount = paymentDTOList.size();

					    if (productCount > 1) {
					        productName = paymentDTOList.get(0).getName()+"외 " + (productCount - 1) + "개";
					    } else if (productCount == 1) {
					        productName = paymentDTOList.get(0).getName();
					    }
					    
				PointHistoryEntity historyEntity = new PointHistoryEntity();
	            historyEntity.setMemberEntity(memberEntity);
	            historyEntity.setUpdatetime(paymentDate);
	            historyEntity.setPointupdown(-userPoint);
	            String explanation="결제시 사용한 포인트";
	            historyEntity.setProductname(productName);
	            historyEntity.setExplanation(explanation);			   
	            pointHistoryRepository.save(historyEntity); 
			    }			 
			    
	            // 포인트 획득 시
			    for (PaymentDTO paymentDTO : paymentDTOList) {
			        int paidAmount = paymentDTO.getPaid_amount();
			        int individualPoint = (int) (paidAmount * 0.1); // 각 상품의 10% 적립
			        updatedPoint += individualPoint;
			        
			    // 포인트 적립  
				    pointEntity.setMypoint(updatedPoint);
				    pointRepository.save(pointEntity); 
			    // 포인트 히스토리 
	            PointHistoryEntity historyEntityGain = new PointHistoryEntity();
	            historyEntityGain.setMemberEntity(memberEntity); 
	            historyEntityGain.setUpdatetime(paymentDate); 
	            historyEntityGain.setPointupdown(individualPoint);
	            String gainname = paymentDTO.getName();
	            historyEntityGain.setProductname(gainname);
	            String gainExplanation = "결제시 얻은 포인트"; 
	            historyEntityGain.setExplanation(gainExplanation);
	            pointHistoryRepository.save(historyEntityGain); 
	            
			   }			    
			  					
		        System.out.println("결제 정보가 성공적으로 저장되었습니다.");
		} catch (Exception e) {
			System.err.println("결제 정보 저장에 실패했습니다.");
	        e.printStackTrace();
	        }
	}			
}	

	
    
   


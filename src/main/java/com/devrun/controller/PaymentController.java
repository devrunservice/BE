package com.devrun.controller;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.PaymentDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.PaymentEntity;
import com.devrun.entity.PointEntity;
import com.devrun.entity.PointHistoryEntity;
import com.devrun.repository.CouponregicodeRepository;
import com.devrun.repository.MemberEntityRepository;
import com.devrun.repository.PaymentInfo;
import com.devrun.repository.PaymentRepository;
import com.devrun.repository.PointHistoryRepository;
import com.devrun.repository.PointRepository;
import com.devrun.service.MemberService;
import com.devrun.service.PaymentService;
import com.devrun.util.JWTUtil;
import com.devrun.youtube.Lecture;
import com.devrun.youtube.LectureRepository;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@RestController
public class PaymentController {
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private PointRepository pointRepository;
	
	@Autowired
	private MemberEntityRepository memberEntityRepository;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private CouponregicodeRepository couponregicodeRepository;
	
	@Autowired
	private PointHistoryRepository pointHistoryRepository;
	
	@Autowired
	private LectureRepository lectureRepository;

	// 결제 정보 db에 저장
	@PostMapping("/savePaymentInfo")
	@ApiOperation("결제 완료 시 db에 필요한 정보들을 저장합니다.")
	 public ResponseEntity<String> savePaymentInfo(@RequestBody List<PaymentDTO> paymentDTOList) {
				
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
			        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자의 포인트 정보가 없습니다.");
			    }

			    // 사용자의 포인트에서 지불할 금액 계산
			    int userPoint = 0;
			    for (PaymentDTO paymentDTO : paymentDTOList) {
			        userPoint += paymentDTO.getMypoint();
			    }

			    int nowPoint = pointEntity.getMypoint();

			    if (nowPoint < userPoint) {
			        // 사용자의 포인트가 부족하여 처리할 수 없는 경우 에러 응답 반환
			        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("보유한 포인트 보다 사용 할 포인트가 더 많습니다.");
			    }

			    // 사용자의 포인트에서 지불할 금액만큼 차감
			    int updatedPoint = nowPoint - userPoint;
			    pointEntity.setMypoint(updatedPoint);
			    pointRepository.save(pointEntity); 
			    
			    //외래키에 값 넣어주기.
			    //결제 정보 사용자 이름으로 memberEntity에서 찾은후, 밑에 추가해주기. 
			    //외래키가 user_no지만 memberEntity로 정의해서 저렇게 넣어줘야함.
			    MemberEntity memberEntity = memberEntityRepository.findByUserNo(usrno);
			    String lecturename = paymentDTOList.get(0).getName();
			    Lecture lecture = lectureRepository.findByLectureName(lecturename);
			    System.err.println(lecture);
			    System.err.println(memberEntity);
				
				try {			
					List<PaymentEntity> paymentList = new ArrayList<>();
			        for (PaymentDTO paymentDTO : paymentDTOList) {
			        PaymentEntity paymentEntity = new PaymentEntity();
			        paymentEntity.setName(paymentDTO.getName());
			        paymentEntity.setPaid_amount(paymentDTO.getPaid_amount());
		            paymentEntity.setBuyer_email(paymentDTO.getBuyer_email());
		            paymentEntity.setBuyer_name(paymentDTO.getBuyer_name());
		            paymentEntity.setImp_uid(paymentDTO.getImp_uid());
		            paymentEntity.setMerchant_uid(paymentDTO.getMerchant_uid());
		            paymentEntity.setReceipt_url(paymentDTO.getReceipt_url());
		            paymentEntity.setBuyer_tel(paymentDTO.getBuyer_tel()); 
					paymentEntity.setPaymentDate(paymentDate);
					paymentEntity.setStatus("0");	
					System.out.println(paymentEntity);	
					paymentEntity.setMemberEntity(memberEntity);
					paymentEntity.setLecture(lecture);					
					
		            paymentList.add(paymentEntity);	            
			       }		    
				    pointRepository.save(pointEntity);			    
			        	System.err.println("-----------------정보저장--------------");
				        System.err.println(paymentList);
						paymentService.savePaymentInfo(paymentList);
						System.err.println(pointEntity);				
					   
					  
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
					
					return ResponseEntity.ok("결제 정보가 성공적으로 저장되었습니다.");
				} catch (Exception e) {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 정보 저장에 실패했습니다.");
				}
			}	
		
		//구매 정보 페이지
		
		@GetMapping("/PaymentInfo")
		@ApiOperation("구매 정보 페이지, 로그인시 토큰에 들어있는 ID값을 가져와서 사용자 정보를 가져옵니다.")
		@ApiImplicitParams({
			@ApiImplicitParam(name = "page", value= "필요한 페이지" , paramType = "header",dataTypeClass = Integer.class, example = "1"),
			@ApiImplicitParam(name = "size", value= "각 페이지에 표시할 항목 수", paramType = "header",dataTypeClass = Integer.class, example = "10")
		})
		public ResponseEntity<?> tmi(@RequestParam("page") int page, @RequestParam("size") int size,					
				HttpServletRequest request) {
			
			 // refreshToken이 헤더에 있는지 확인
		    String accessToken = request.getHeader("Access_token");

		    // Refresh Token 존재 여부 확인 (null 혹은 빈문자열 인지 확인)
		    if (accessToken == null || accessToken.isEmpty()) {
		        // 400 : Access token 없음
		        return new ResponseEntity<>("Access token is required", HttpStatus.BAD_REQUEST);
		    }

		    String id = JWTUtil.getUserIdFromToken(accessToken);
		    
		        MemberEntity member = memberService.findById(id);	
		        
		        int usrno = member.getUserNo();		
		        
		        PageRequest pageRequest = PageRequest.of(page -1, size);
		        

		        // 사용자의 고유번호로 결제 정보 조회
		        Page<PaymentInfo> paymentsPage = paymentRepository.findAllbyPaymentEntity(usrno,pageRequest);
		        System.err.println(paymentsPage);

		        if (paymentsPage.isEmpty()) {
		            // 결제 정보가 없을 경우에 대한 처리
		            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("결제 정보가 없습니다.");
		        }

		        return ResponseEntity.ok(paymentsPage);
		}

		
	
}

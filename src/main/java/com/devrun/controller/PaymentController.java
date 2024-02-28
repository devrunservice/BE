package com.devrun.controller;



import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.dto.PaymentDTO;
import com.devrun.dto.FreeDTO;
import com.devrun.dto.MentoMoneyDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.repository.PaymentInfo;
import com.devrun.repository.PaymentRepository;
import com.devrun.service.MemberService;
import com.devrun.service.MentoMoneyService;
import com.devrun.service.MyLectureService;
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
	private MemberService memberService;
	
	@Autowired
	private LectureRepository lectureRepository;
	
	@Autowired
	private MyLectureService myLectureService;
	
	@Autowired
	private MentoMoneyService mentoMoneyService;

	// 결제 정보 db에 저장
	@PostMapping("/savePaymentInfo")
	@ApiOperation("결제 완료 시 db에 필요한 정보들을 저장합니다.")
	 public ResponseEntity<String> savePaymentInfo(@RequestBody List<PaymentDTO> paymentDTOList) {
		try {
			paymentService.savePayment(paymentDTOList);
	        return ResponseEntity.ok("결제 정보가 성공적으로 저장되었습니다.."); 
			
		}catch (Exception e) {
		     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 정보 저장 오류");
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
		            return ResponseEntity.status(HttpStatus.OK).body("결제 정보가 없습니다.");
		        }

		        return ResponseEntity.ok(paymentsPage);
		}
		
		@PostMapping("/Free")
		@ApiOperation("무료 강의 API 입니다")
		public ResponseEntity<?> freelecture(@RequestBody FreeDTO freeDTO) {
		        
		        String userid = SecurityContextHolder.getContext().getAuthentication().getName();
				MemberEntity memberEntity = memberService.findById(userid);
				
		        System.err.println(memberEntity);
		        String lectureName = freeDTO.getLectureName();
		        Lecture lecture = lectureRepository.findByLectureName(lectureName);
		        myLectureService.registLecture(memberEntity, lecture);
		        
		        return ResponseEntity.ok("성공");		        
		   
		}
		
		@GetMapping("/MentoMoney")
		public ResponseEntity<?> mentoMoney(@RequestParam("page") int page){
	        String userid = SecurityContextHolder.getContext().getAuthentication().getName();
	        MemberEntity member = memberService.findById(userid);	 
	        int usrno = member.getUserNo();
	        Pageable pageable = PageRequest.of(page - 1, 10);
		    Page<MentoMoneyDTO> resultPage = mentoMoneyService.searchMoney(usrno, pageable);
		    
			return ResponseEntity.ok(resultPage);
		
		}

		

		
	
}

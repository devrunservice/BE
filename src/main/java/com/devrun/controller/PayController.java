package com.devrun.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.entity.CouponIssued;
import com.devrun.entity.PaymentEntity;
import com.devrun.repository.CouponIssuedRepository;
import com.devrun.repository.PaymentRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

@RestController
public class PayController {
	
//	@Value("${iamport_KEY}")
//	private String KEY;
//	
//	@Value("${iamport_SECRET}")
//	private String SECRET;
	
	private String KEY="4540181673513222";
	private String SECRET="DIgkSQTsMXqTRNmRsTzZtDmFPesaS7XfGBiOzOYWE7OKEHyTpdlqr7hxhObgMsdAV9NkyytcVmXXJ1Si";
	
	private  IamportClient api;	
	
//	@Autowired
//	private CouponIssuedRepository CouponIssuedRepository;
//	
//	@Autowired
//	private PaymentRepository PaymentRepository;

	@ResponseBody
	// 아임포트 api 문서를 예시로 값 넣어주기
	@PostMapping("/verifyIamport/{imp_uid}")
	public ResponseEntity<?> paymentByImpUid(
	        Model model,
	        Locale locale,
	        HttpSession session,
	        @PathVariable(value = "imp_uid") String imp_uid
//	        ,@RequestParam(value = "couponCode") String couponCode
	        ) {
	    try {
	    	System.err.println(imp_uid);
			// 아임포트 나의 정보 값 넣기	    
	        this.api = new IamportClient(KEY, SECRET);
	        System.err.println(KEY);
	        System.err.println(SECRET);
	        
			//아임포트 서버에 imp_uid를 통해 값 받아와서 우리 서버랑 비교 후 같으면 결제 진행.
	        IamportResponse<Payment> response = api.paymentByImpUid(imp_uid);
	        
	        
	        //쿠폰 적용시
//	        if(couponCode !=null) {
//	            CouponIssued couponIssued = CouponIssuedRepository.findByDiscountrate(couponCode);
//		        PaymentEntity paymentEntity = PaymentRepository.findByPaidAmount(imp_uid);
//
//	            int orgAmount =paymentEntity.getPaid_amount();	            		
//	            int discountAmount =couponIssued.getDiscountrate();
//	            int finallyAmount = orgAmount - discountAmount;
//	            paymentEntity.setPaid_amount(finallyAmount);
//	        }
	        
	        return ResponseEntity.ok(response);

	    } catch (IamportResponseException e) {
	        // 아임포트 API 호출 중에 예외가 발생한 경우
	        String errorMessage = "아임포트 API 오류: " + e.getMessage();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
	    } catch (IOException e) {
	        // 입출력 예외가 발생한 경우
	        String errorMessage = "입출력 오류: " + e.getMessage();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
	    }
	}
	
	

}

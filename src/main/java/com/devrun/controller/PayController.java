package com.devrun.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import io.swagger.annotations.ApiOperation;

@RestController
public class PayController {
	
	@Value("${IAMPORT_KEY}")
	private String KEY;
	
	@Value("${IAMPORT_SECRET}")
	private String SECRET;	
	
	private  IamportClient api;		

	@ResponseBody
	// 아임포트 api 문서를 예시로 값 넣어주기
	@PostMapping("/verifyIamport/{imp_uid}")
    @ApiOperation("결제 검증, 결제 번호를 통하여 아임포트 서버에서 값을 받아와서 우리 서버의 요청값과 비교 후 같으면 진행합니다.(결제금액의 위변조 검증)")

	public ResponseEntity<?> paymentByImpUid( Model model, Locale locale, HttpSession session,
			@PathVariable(value = "imp_uid") String imp_uid) {
		
	    try {
	    	System.err.println(imp_uid);
   
	        this.api = new IamportClient(KEY, SECRET);
	        System.err.println(KEY);
	        System.err.println(SECRET);
	        
			//아임포트 서버에 imp_uid를 통해 값 받아와서 우리 서버랑 비교 후 같으면 결제 진행.
	        IamportResponse<Payment> response = api.paymentByImpUid(imp_uid);
	        
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

package com.devrun.controller;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

@RestController
public class PayController {
	@Value("${iamport.KEY}")
	private String KEY;
	
	@Value("${iamport.SECRET_Key}")
	private String SECRET;
	
	private  IamportClient api;	

	@ResponseBody
	// 아임포트 api 문서를 예시로 값 넣어주기
	@RequestMapping(value="/verifyIamport/{imp_uid}")
	public IamportResponse<Payment> paymentByImpUid(
			Model model
			, Locale locale
			, HttpSession session
			, @PathVariable(value= "imp_uid") String imp_uid) throws IamportResponseException, IOException
	{	
		// 아임포트 나의 정보 값 넣기
			this.api = new IamportClient(KEY,SECRET);
		//아임포트 서버에 imp_uid를 통해 값 받아와서 우리 서버랑 비교 후 같으면 결제 진행.

			return api.paymentByImpUid(imp_uid);
	}
	
	

}

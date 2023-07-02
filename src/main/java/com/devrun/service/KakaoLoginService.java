package com.devrun.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.devrun.dto.OAuthToken;
import com.devrun.dto.member.KakaoProfileDTO;
import com.devrun.dto.member.LogoutResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class KakaoLoginService {
	
	// RestTemplate를 Non-Blocking과 비동기처리가 가능한 WebClinet로 대체
	// RestTemplate를 Non-Blocking과 비동기처리가 가능한 WebClinet로 대체
	// RestTemplate를 Non-Blocking과 비동기처리가 가능한 WebClinet로 대체
	// RestTemplate를 Non-Blocking과 비동기처리가 가능한 WebClinet로 대체
	// RestTemplate를 Non-Blocking과 비동기처리가 가능한 WebClinet로 대체
	
	// @Value를 이용해 application.properties에서 가져와 유지보수도 쉽고 보안성도 유지할 수 있다
	@Value("${kakao.client_id}")
	private String client_id;

	@Value("${kakao.redirect_url}")
	private String redirect_url;
	
    public OAuthToken getOauthToken(String code) {
    	// https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token
		// POST방식으로 key=value 데이터를 요청(to카카오)
		// Retrofit2, OkHttp, RestTemplatet 방식이 있는데 그 중에서 RestTemplatet를 사용
		RestTemplate rt = new RestTemplate();									// Spring에서 제공하는 HTTP 클라이언트입니다. 이를 사용하여 HTTP 요청을 보내고 응답을 받을 수 있습니다.
		
		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");	// HTTP 요청의 헤더를 설정
		
		
		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();		// MultiValueMap이 사용된 이유는 일반적인 Map을 사용했을 때와 몇 가지 차이점이 있습니다
		params.add("grant_type", "authorization_code");							// MultiValueMap은 하나의 키에 대해 여러 개의 값을 가지는 경우를 고려한 설계입니다.
		params.add("client_id", client_id);										// 이 예제에서는 각 키에 대해 하나의 값만을 가지지만, 여러 개의 값을 가질 수 있는 상황을 대비하여 MultiValueMap을 사용할 수 있습니다.
		params.add("redirect_url", redirect_url);								// HttpEntity나 RestTemplate 등의 Spring Framework의 일부 기능에서는 MultiValueMap을 사용하는 경우가 많습니다.
		params.add("code", code);												// 특히 HTTP 요청의 헤더나 폼 데이터를 다룰 때 MultiValueMap이 편리하게 사용될 수 있습니다.
																				// LinkedMultiValueMap 같은 MultiValueMap의 구현체는 키와 값의 순서를 보장합니다.
																				// 일반적인 HashMap은 키의 순서를 보장하지 않으므로, 순서가 중요한 경우에는 LinkedMultiValueMap을 사용할 수 있습니다.
													
		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		// exchange가 HttpEntity라는 오브젝트를 갖기 때문
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);	// 전송할 HTTP 요청 본문을 생성하는 부분입니다. 
																											// 위에서 설정한 헤더와 함께 필요한 파라미터들이 들어갑니다.
		// POST방식으로 Http 요청하고 response 변수의 응답을 받음
		ResponseEntity<String> response = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST, kakaoTokenRequest, String.class);
																											// 실제로 HTTP 요청을 보내는 부분입니다.
																											// 카카오의 토큰 발급 서버에 POST 요청을 보내고, 응답을 ResponseEntity<String> 타입으로 받습니다.
		// JSON > Object
		// JSON parse를 하지 않아도 된다
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);	// 카카오의 응답을 Java 객체로 변환하는 부분입니다.
																						// 응답 본문은 JSON 형식이므로 이를 OAuthToken 클래스의 인스턴스로 변환합니다.
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		System.out.println("oauthToken : " + oauthToken);
		return oauthToken;
    }

    public KakaoProfileDTO getKakaoProfile(OAuthToken oauthToken) {
    	// https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
		// 마지막으로, 사용자의 프로필 정보를 얻기 위해 한 번 더 카카오 API에 요청을 보내고, 이를 kakaoProfile 객체로 변환합니다.
		// RestTemplatet
		RestTemplate rt2 = new RestTemplate();
		
		// HttpHeader 오브젝트 생성
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("authorization", "Bearer " + oauthToken.getAccess_token());
		headers2.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// exchange가 HttpEntity라는 오브젝트를 갖기 때문
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers2);
		
		// POST방식으로 Http 요청하고 response 변수의 응답을 받음
		ResponseEntity<String> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, kakaoProfileRequest, String.class);
		
//		{
//		    "id":2843888591,  // 카카오에서 사용자를 고유하게 식별하기 위한 ID입니다.
//		    "connected_at":"2023-06-14T21:41:37Z",  // 사용자가 카카오 계정과 앱을 연결한 시간입니다.
//		    "properties":{  // 사용자의 프로필 정보를 포함하는 객체입니다.												어플에서 설정한 데이터
//		        "nickname":"성민",  // 사용자의 닉네임입니다.
//		        "profile_image":"http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg",  // 사용자의 프로필 이미지 URL입니다. 640x640 크기입니다.
//		        "thumbnail_image":"http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_110x110.jpg"  // 사용자의 프로필 썸네일 이미지 URL입니다. 110x110 크기입니다.
//		    },
//		    "kakao_account":{  // 사용자의 카카오 계정 정보를 포함하는 객체입니다.											계정에 설정된 데이터
//		        "profile_nickname_needs_agreement":false,  // 사용자가 닉네임 공유에 동의했는지 여부입니다.				false가 동의??
//		        "profile_image_needs_agreement":false,  // 사용자가 프로필 이미지 공유에 동의했는지 여부입니다.
//		        "profile":{  // 사용자의 카카오 계정 프로필 정보를 포함하는 객체입니다.
//		            "nickname":"성민",  // 사용자의 닉네임입니다.
//		            "thumbnail_image_url":"http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_110x110.jpg",  // 사용자의 프로필 썸네일 이미지 URL입니다. 110x110 크기입니다.
//		            "profile_image_url":"http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_640x640.jpg",  // 사용자의 프로필 이미지 URL입니다. 640x640 크기입니다.
//		            "is_default_image":true  // 사용자가 기본 이미지를 사용하고 있는지 여부입니다.
//		        },
//		        "has_email":true,  // 사용자가 이메일 주소를 가지고 있는지 여부입니다.
//		        "email_needs_agreement":false,  // 사용자가 이메일 주소 공유에 동의했는지 여부입니다.
//		        "is_email_valid":true,  // 사용자의 이메일 주소가 유효한지 여부입니다.									유효하고 검증되었을 경우만 회원정보와 매칭하는것이 안전
//		        "is_email_verified":true,  // 사용자의 이메일 주소가 검증되었는지 여부입니다.
//		        "email":"sung0763@naver.com"  // 사용자의 이메일 주소입니다.
//		    }
//		}

		
		// JSON > Object
		ObjectMapper objectMapper2 = new ObjectMapper();
		KakaoProfileDTO kakaoProfile = null;
		try {
			kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfileDTO.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return kakaoProfile;
    }
    
    public LogoutResponse kakaoLogout(String token, Long id) {
    	// https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
		// 마지막으로, 사용자의 프로필 정보를 얻기 위해 한 번 더 카카오 API에 요청을 보내고, 이를 kakaoProfile 객체로 변환합니다.
		// RestTemplatet
		RestTemplate rt3 = new RestTemplate();
		System.out.println("로그아웃 아이디 : " + id + " 토큰 : " + token);
		// HttpHeader 오브젝트 생성
		HttpHeaders headers3 = new HttpHeaders();
		headers3.add("authorization", "Bearer " + token);
		
		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params3 = new LinkedMultiValueMap<>();
		params3.add("target_id_type", "user_id");
		params3.add("target_id", id.toString());
		
		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		// exchange가 HttpEntity라는 오브젝트를 갖기 때문
		HttpEntity<MultiValueMap<String, String>> kakaoLogoutRequest = new HttpEntity<>(params3, headers3);
		
		// POST방식으로 Http 요청하고 response 변수의 응답을 받음
		ResponseEntity<String> response3 = rt3.exchange("https://kapi.kakao.com/v1/user/logout", HttpMethod.POST, kakaoLogoutRequest, String.class);
		System.out.println("리스폰즈 : " + response3);
		// 실제로 HTTP 요청을 보내는 부분입니다.
		// 카카오의 토큰 발급 서버에 POST 요청을 보내고, 응답을 ResponseEntity<String> 타입으로 받습니다.
		// JSON > Object
		ObjectMapper objectMapper3 = new ObjectMapper();
		LogoutResponse logoutResponse = null;
		try {
			logoutResponse = objectMapper3.readValue(response3.getBody(), LogoutResponse.class);	// 카카오의 응답을 Java 객체로 변환하는 부분입니다.
		// 응답 본문은 JSON 형식이므로 이를 OAuthToken 클래스의 인스턴스로 변환합니다.
		} catch (JsonMappingException e) {
		e.printStackTrace();
		} catch (JsonProcessingException e) {
		e.printStackTrace();
		}
		System.out.println("kakaoId : " + logoutResponse);
		return logoutResponse;
    }
    
}

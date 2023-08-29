package com.devrun.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.devrun.util.CaffeineCache;

import net.bytebuddy.utility.RandomString;

@Service
public class EmailSenderService {
	
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private CaffeineCache cacheService;
    
    String imgTag = "";
    String bodyTop = "<!DOCTYPE html>" +
    		"<html lang=\"ko\">" +
    		"<head>" +
    		"<meta charset=\"utf-8\" />" +
    		"<link rel=\"icon\" href=\"fabicon.png\" />" +
    		"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />" +
    		"<title>DevRun</title>" +
    		"<style>" +
    		"@font-face {" +
    		"  font-family: \"Pretendard\";" +
    		"  src: url(\"https://cdn.jsdelivr.net/gh/Project-Noonnu/noonfonts_2107@1.1/Pretendard-Regular.woff\");" +
    		"  font-weight: 400;" +
    		"}" +
    		"@font-face {" +
    		"  font-family: \"Pretendard\";" +
    		"  src: url(\"https://cdn.jsdelivr.net/gh/Project-Noonnu/noonfonts_2107@1.1/Pretendard-Medium.woff\");" +
    		"  font-weight: 500;" +
    		"}" +
    		"@font-face {" +
    		"  font-family: \"Pretendard\";" +
    		"  src: url(\"https://cdn.jsdelivr.net/gh/Project-Noonnu/noonfonts_2107@1.1/Pretendard-SemiBold.woff\");" +
    		"  font-weight: 600;" +
    		"}" +
    		"@font-face {" +
    		"  font-family: \"Pretendard\";" +
    		"  src: url(\"https://cdn.jsdelivr.net/gh/Project-Noonnu/noonfonts_2107@1.1/Pretendard-Bold.woff\");" +
    		"  font-weight: 700;" +
    		"}" +
    		"</style>" +
    		"</head>" +
    		"<body>" +
    		"<div id=\"root\" style=\"background:#f7f7f7;width:100%;padding:50px 0;\">" +
    		"<div style=\"background:#fff;margin:0 auto;width:640px;\">";
    
    String bodyBottom = "</div>" +
    		"</div>" +
    		"<div style=\"border-top:1px solid #ddd;\">" +
    		"<div style=\"padding:25px 40px;\">" +
    		"<p style=\"margin: 0;font-size: 0.875rem;margin-bottom:5px;font-family: \"Pretendard\";font-weight:400; color:#171717;\">본메일은 발신전용입니다.</p>" +
    		"<p style=\"margin: 0;font-size: 0.875rem;margin-bottom:5px;font-family: \"Pretendard\";font-weight:400; color:#171717;\">DevRun에 관하여 궁금한 점이 있으시다면 <span>고객센터</span>로 문의해주세요</p>" +
    		"<p style=\"margin: 0;font-size: 0.875rem;font-family: \"Pretendard\";font-weight:400; color:#171717;\">© 2023  DEVRUN All rights reserved</p>" +
    		"</div>" +
    		"</div>" +
    		"</div>" +
    		"</div>" +
    		"</body>" +
    		"</html>";
    
    public void sendSignupByEmail(String toEmail, String id) {
    	MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
    	
        // 경로에 파일이 없거나 파일을 읽을 권한이 없을 경우 예외처리를 하기 위해 try/catch를 사용
        try {
        	// dataURL을 사용하여 이미지 첨부
        	InputStream in = getClass().getResourceAsStream("/img/logo.png");
        	byte[] imageBytes = in.readAllBytes();
            String encodedString = Base64.getEncoder().encodeToString(imageBytes);
            imgTag = "<img src=\"data:image/png;base64," + encodedString + "\" alt=\"devrun로고\" style=\\\"width:144px,height:144px;\"/>";
		} catch (IOException e) {
			System.out.println("이미지 인코딩 실패");
			e.printStackTrace();
		}

        
        String subject = "[Devrun] " + id + "님 회원가입을 축하합니다. 이메일 인증을 완료해주세요.";
        String url = "https://devrun.net/signup/ok";
        RandomString rs = new RandomString(35);
        String tempkey = rs.nextString();
        
        String body = bodyTop +
        		
         		"<div style=\"background: #5F4B8B; font-size: 0; padding: 0 30px; height: 100px; line-height:100px; \">" + imgTag + "</div>" +
         		"<div style=\"padding:40px 40px 60px\">" +
                "<h3 style=\"font-size:1.56rem;color:#171717;line-height: 1;margin:0;margin-bottom:25px; font-family: \"Pretendard\";font-weight:700;\">DevRun 회원가입을 축하드립니다.</h3>" +
                "<p style=\"font-size:1rem;color:#676767;line-height: 1;margin:0; font-family: \"Pretendard\";font-weight:400;\">아래 링크를 클릭하여 회원가입을 완료해 주세요.</p>" +
                "<div style=\"border-top:1px solid #ddd; border-bottom:1px solid #ddd; padding: 25px 0;margin-top: 35px;\">" +
                
				"<form id=\"confirmationForm\" method=\"post\" action=\"" + url + "\">" +
				"<input type='hidden' id='id' name='id' value='" + id + "'/>" +
				"<input type='hidden' id='key' name='key' value='" + tempkey + "'/>" +
				"<p style=\"font-size: 0; padding:0px 30px;display: flex;align-items: center;justify-content: center;\">" +
				"<button type=\"submit\" style=\"background-color: #5F4B8B; color: #FFFFFF; width: 140px; height: 45px; text-align: center; border: none; font-family: 'Pretendard'; font-weight: 400; display: flex; align-items: center; justify-content: center;\"><b>이메일 인증하기</b></button>" +
				"</p>" +
				"</form>" + 
				
				bodyBottom;
        
        System.out.println("EmailSenderService 이메일주소 : " + toEmail);
        
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom("emaildragon2@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // Set the second parameter to 'true' to send HTML content

            cacheService.saveCaffeine(id, tempkey);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
    public void sendFindByEmail(String toEmail, String id) {
    	MimeMessage message = mailSender.createMimeMessage();
    	MimeMessageHelper helper;
    	
    	// 경로에 파일이 없거나 파일을 읽을 권한이 없을 경우 예외처리를 하기 위해 try/catch를 사용
    	try {
    		// dataURL을 사용하여 이미지 첨부
    		InputStream in = getClass().getResourceAsStream("/img/logo.png");
    		byte[] imageBytes = in.readAllBytes();
    		String encodedString = Base64.getEncoder().encodeToString(imageBytes);
    		imgTag = "<img src=\"data:image/png;base64," + encodedString + "\" alt=\"devrun로고\" style=\\\"width:144px,height:144px;\"/>";
    	} catch (IOException e) {
    		System.out.println("이미지 인코딩 실패");
    		e.printStackTrace();
    	}
    	
    	
    	String subject = "[Devrun] " + id + "님 이메일 인증을 완료해주세요.";
    	Random rs = new Random();
    	String key = String.valueOf(rs.nextInt(6) + 1) ;
    	
    	String body = bodyTop +
    			
	    		"<div style=\"background: #5F4B8B; font-size: 0; padding: 0 30px; height: 100px; line-height:100px; \">" + imgTag + "</div>" +
	    		"<div style=\"padding:40px 40px 60px\">" +
    			"<h3 style=\"font-size:1.56rem;color:#171717;line-height: 1;margin:0;margin-bottom:25px; font-family: \"Pretendard\";font-weight:700;\">DevRun 인증번호 안내입니다.</h3>" +
    			"<p style=\"font-size:1rem;color:#676767;line-height: 1;margin:0; font-family: \"Pretendard\";font-weight:400;\">아래 인증번호를 입력해 주세요.</p>" +
    			"<div style=\"border-top:1px solid #ddd; border-bottom:1px solid #ddd; padding: 25px 0;margin-top: 35px;\">" +
    			
				"<input type='hidden' id='id' name='id' value='" + id + "'/>" +
				"<input type='hidden' id='key' name='key' value='" + key + "'/>" +
                "<p style=\"font-size: 0; padding:0px 30px;display: flex;align-items: center;\">" +
                "<span style=\"font-size: 1rem; width:130px; font-family: 'Pretendard';font-weight:400; color:#171717; display: block;\">인증번호</span>" +
                "<span style=\"font-size: 1rem;font-family: 'Pretendard';font-weight:500; color:#171717;display: block;\">" + key + "</span>" +
                "</p>" +
				
				bodyBottom;
    	
    	System.out.println("EmailSenderService 이메일주소 : " + toEmail);
    	
    	try {
    		helper = new MimeMessageHelper(message, true);
    		helper.setFrom("emaildragon2@gmail.com");
    		helper.setTo(toEmail);
    		helper.setSubject(subject);
    		helper.setText(body, true); // Set the second parameter to 'true' to send HTML content
    		
    		cacheService.saveCaffeine(id, key);
    		mailSender.send(message);
    	} catch (MessagingException e) {
    		e.printStackTrace();
    	}
    }
    
    public void sendIdByEmail(String toEmail, String id) {
    	MimeMessage message = mailSender.createMimeMessage();
    	MimeMessageHelper helper;
    	String imgTag = "";
    	
    	// 경로에 파일이 없거나 파일을 읽을 권한이 없을 경우 예외처리를 하기 위해 try/catch를 사용
    	try {
    		// dataURL을 사용하여 이미지 첨부
    		InputStream in = getClass().getResourceAsStream("/img/logo.png");
    		byte[] imageBytes = in.readAllBytes();
    		String encodedString = Base64.getEncoder().encodeToString(imageBytes);
    		imgTag = "<img src=\"data:image/png;base64," + encodedString + "\" alt=\"devrun로고\" style=\"width:144px,height:144px;\"/>";
    	} catch (IOException e) {
    		System.out.println("이미지 인코딩 실패");
    		e.printStackTrace();
    	}
    	
    	
    	String subject = "[Devrun] 아이디를 확인해주세요.";
    	Random rs = new Random();
    	String key = String.valueOf(rs.nextInt(6) + 1);
    	
    	String body = bodyTop +
    			
	    		"<div style=\"background: #5F4B8B; font-size: 0; padding: 0 30px; height: 100px; line-height:100px; \">" + imgTag + "</div>" +
	    		"<div style=\"padding:40px 40px 60px\">" +
    			"<h3 style=\"font-size:1.56rem;color:#171717;line-height: 1;margin:0;margin-bottom:25px; font-family: \"Pretendard\";font-weight:700;\">DevRun 아이디찾기 안내입니다.</h3>" +
    			"<p style=\"font-size:1rem;color:#676767;line-height: 1;margin:0; font-family: \"Pretendard\";font-weight:400;\">아래 아이디를 확인해 주세요.</p>" +
    			"<div style=\"border-top:1px solid #ddd; border-bottom:1px solid #ddd; padding: 25px 0;margin-top: 35px;\">" +
    			
				"<p style=\"font-size: 0; padding:0px 30px;display: flex;align-items: center;\">" +
				"<span style=\"font-size: 1rem; width:130px; font-family: 'Pretendard';font-weight:400; color:#171717; display: block;\">아이디</span>" +
				"<span style=\"font-size: 1rem;font-family: 'Pretendard';font-weight:500; color:#171717;display: block;\">" + id + "</span>" +
				"</p>" +
				
				bodyBottom;
    	
    	System.out.println("EmailSenderService 이메일주소 : " + toEmail);
    	
    	try {
    		helper = new MimeMessageHelper(message, true);
    		helper.setFrom("emaildragon2@gmail.com");
    		helper.setTo(toEmail);
    		helper.setSubject(subject);
    		helper.setText(body, true); // Set the second parameter to 'true' to send HTML content
    		
    		cacheService.saveCaffeine(id, key);
    		mailSender.send(message);
    	} catch (MessagingException e) {
    		e.printStackTrace();
    	}
    }
}

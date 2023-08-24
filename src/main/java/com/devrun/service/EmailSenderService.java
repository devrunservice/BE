package com.devrun.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import net.bytebuddy.utility.RandomString;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private CacheService cacheService;

    public void sendEmail(String toEmail, String id) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        
        String subject = "회원가입 이메일 인증 안내";
        String url = "https://devrun.net/signup/ok";
        RandomString rs = new RandomString(35);
        String key = rs.nextString();
        
        String body = "<!DOCTYPE html>" +
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
                "<div style=\"background:#fff;margin:0 auto;width:640px;\">" +
                "<div style=\"background: #5F4B8B; font-size: 0; padding: 0 30px;  height: 100px; display: flex; align-items: center;\"><img src=\"" + logo_image + "\" alt=\"devrun로고\"/></div>" +
                "<div style=\"padding:40px 40px 60px\">" +
                "<h3 style=\"font-size:1.56rem;color:#171717;line-height: 1;margin:0;margin-bottom:25px; font-family: \"Pretendard\";font-weight:700;\">DevRun 회원가입을 축하드립니다.</h3>" +
                "<p style=\"font-size:1rem;color:#676767;line-height: 1;margin:0; font-family: \"Pretendard\";font-weight:400;\">아래 링크를 클릭하여 회원가입을 완료해 주세요.</p>" +
                "<div style=\"border-top:1px solid #ddd; border-bottom:1px solid #ddd; padding: 25px 0;margin-top: 35px;\">" +
                "<p style=\"font-size: 0; padding:0px 30px;margin-bottom:15px; display: flex;align-items: center;\">" +
                "<span style=\"font-size: 1rem; width:130px; font-family: \"Pretendard\";font-weight:400; color:#171717;  display: block;\">DevRun계정</span>" +
                "<span style=\"font-size: 1rem;font-family: \"Pretendard\";font-weight:500; color:#5F4B8B;display: block;text-decoration: underline;\">"+ id +"</span></p>" +
                
				"<form id=\"confirmationForm\" method=\"post\" action=\"" + url + "\">" +
				"<input type='hidden' id='id' name='id' value='" + id + "'/><br>" +
				"<input type='hidden' id='key' name='key' value='" + key + "'/><br>" +
				"<p style=\"font-size: 0; padding:0px 30px;display: flex;align-items: center;justify-content: center;\">" +
				"<button type=\"submit\" style=\"background-color: #5F4B8B; color: #FFFFFF; width: 140px; height: 45px; text-align: center; border: none; font-family: 'Pretendard'; font-weight: 400; display: flex; align-items: center; justify-content: center;\"><b>이메일 인증하기</b></button>" +
				"</p>" +
				"</form>" + 
                
                "</div>" +
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
        
        System.out.println("EmailSenderService 이메일주소 : " + toEmail);
        
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom("emaildragon2@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // Set the second parameter to 'true' to send HTML content

            mailSender.send(message);
            cacheService.saveEmailVerifyTempKey(id, key);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

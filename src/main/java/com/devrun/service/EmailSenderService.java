package com.devrun.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
	
    @Autowired
    JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        System.out.println(toEmail + "EmailSenderService 이메일");
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom("emaildragon2@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // Set the second parameter to 'true' to send HTML content

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
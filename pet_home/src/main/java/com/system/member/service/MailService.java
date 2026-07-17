package com.system.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    public void sendPasswordEmail(String email, String resetToken) {
        String encoderToken = URLEncoder.encode(resetToken, StandardCharsets.UTF_8);
        String resetUrl = frontendBaseUrl + "/reset-password?token=" + encoderToken;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("【毛起來】密碼重設通知");

        message.setText(
                "您好：\n\n" +
                        "我們收到您的密碼重設申請。請點擊以下連結完成密碼重設：\n\n" +
                        resetUrl + "\n\n" +
                        "此連結將於 15 分鐘後失效。\n\n" +
                        "若您未提出此申請，請忽略此信件，您的帳號密碼不會受到影響。\n\n" +
                        "此為系統自動發送信件，請勿直接回覆。\n\n" +
                        "毛起來團隊 敬上"
        );
        mailSender.send(message);
    }

    public void sendEmailVerified(String email, String verifiedToken) {
        String encoderToken = URLEncoder.encode(verifiedToken, StandardCharsets.UTF_8);
        String url = frontendBaseUrl + "/verify-email?token=" + encoderToken;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("【毛起來】 Email 驗證");
        message.setText(
                "您好，\n\n" +
                        "感謝您註冊毛起來！\n\n" +
                        "請點擊下方連結完成 Email 驗證：\n" +
                        url + "\n\n" +
                        "此連結將於 30 分鐘後失效。\n\n" +
                        "如果您沒有註冊毛起來帳號，請忽略這封信。\n\n" +
                        "此為系統自動發送信件，請勿直接回覆。\n\n" +
                        "毛起來團隊");
        mailSender.send(message);
    }

    public void sendEmailChangeConfirm(String email, String token) {
        String encoderToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        String url = frontendBaseUrl + "/email-change-confirm?token=" + encoderToken;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("【毛起來】 更改Email 申請");
        message.setText(
                "您好：\n\n" +
                        "我們收到您的「毛起來」帳號更改 Email 申請。\n\n" +
                        "請點擊以下連結完成新 Email 驗證：\n" +
                        url + "\n\n" +
                        "此連結將於 30 分鐘後失效。\n\n" +
                        "如果這不是您本人操作，請忽略此封信，並建議您盡快登入帳號修改密碼。\n\n" +
                        "毛起來團隊"
        );
        mailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = 
                new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send HTML email to " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package com.system.shop.service;

import com.system.shop.dto.OrderEmailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOrderConfirmationEmail(OrderEmailDTO data) {
        try {
            // 1. 將 DTO 放入 Thymeleaf Context
            Context context = new Context();
            context.setVariable("data", data);

            // 2. 渲染 HTML（對應 resources/templates/email/order-confirmation.html）
            String html = templateEngine.process("email/orderConfirmation", context);

            // 3. 組裝 MimeMessage
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(data.getCustomerEmail());
            helper.setSubject("【毛起來】訂單成立通知 - " + data.getOrderNo());
            helper.setText(html, true);

            mailSender.send(message);
            log.info("訂單確認信寄送成功：{} -> {}", data.getOrderNo(), data.getCustomerEmail());

        } catch (MessagingException e) {
            log.error("訂單確認信寄送失敗 [{}]：{}", data.getOrderNo(), e.getMessage());
        }
    }
}

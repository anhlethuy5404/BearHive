package com.bearhive.bearhive.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpMail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Xác nhận reset mật khẩu");
        message.setText("Mã xác nhận của bạn là: " + otp + ". Mã này có hiệu lực trong vòng 5 phút.");
        mailSender.send(message);
    }
}

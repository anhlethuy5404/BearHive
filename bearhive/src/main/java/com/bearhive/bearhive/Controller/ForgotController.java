package com.bearhive.bearhive.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bearhive.bearhive.Model.Forgot;
import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Repository.ForgotRepository;
import com.bearhive.bearhive.Repository.UserRepository;
import com.bearhive.bearhive.Service.EmailService;
import com.bearhive.bearhive.Service.UserService;



@Controller
public class ForgotController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForgotRepository forgotRepository;
    
    @GetMapping("/forgot")
    public String forgotPassword() {
        return "forgot";
    }

    @PostMapping("/forgot")
    @ResponseBody
    public HashMap<String, String> sendOtp(@RequestParam("email") String email) {
        HashMap<String, String> response = new HashMap<>();
        Optional<User> user = userRepository.findByEmail(email);
        if(!user.isPresent()) {
            response.put("status", "error");
            response.put("message", "Email không tồn tại, vui lòng đăng ký tài khoản!");
            return response;
        }
        String otp = String.format("%06d", new Random().nextInt(100000,999999));
        LocalDateTime expiredDate = LocalDateTime.now().plusMinutes(5);
        User u = user.get();
        Forgot forgot = new Forgot(otp, u, expiredDate);
        forgotRepository.save(forgot);
        forgotRepository.flush();
        emailService.sendOtpMail(email, otp);
        response.put("status", "success");
        response.put("message", "Otp đã được gửi tới email của bạn!");
        return response;
    }

    @GetMapping("/verify")
    public String showVerifyPage(@RequestParam("email") String email, Model model) {
        model.addAttribute("email", email);
        return "verify";
    }
    
    @PostMapping("/verify")
    @Transactional
    public String verifyOtp(@RequestParam("email") String email, @RequestParam("otp") String otp, Model model) {
        Forgot forgot = forgotRepository.findByUserEmail(email);
        if(forgot == null) {
            model.addAttribute("error", "Mã OTP không chính xác!");
            model.addAttribute("email", email);
            return "verify";
        }
        if(!forgot.getOtp().trim().equals(otp.trim())) {
            model.addAttribute("error", "OTP không chính xác!");
            model.addAttribute("email", email);
            return "verify";
        }
        if(LocalDateTime.now().isAfter(forgot.getExpiredDate())) {
            model.addAttribute("error", "OTP đã hết hạn, vui lòng bấm gửi lại OTP!");
            model.addAttribute("email", email);
            return "verify";
        }
        User user = forgot.getUser();
        if(user != null) {
            user.setForgot(null);
            userRepository.save(user);
        }
        forgotRepository.delete(forgot);
        forgotRepository.flush();
        model.addAttribute("email", email);
        return "reset";
    }
    
    @PostMapping("/resend")
    @Transactional
    public String resendOtp(@RequestParam("email") String email, Model model) {
        Forgot forgot = forgotRepository.findByUserEmail(email);
        if(forgot == null) {
            return "verify";
        }
        User user = forgot.getUser();
        if(user != null) {
            user.setForgot(null);
            userRepository.save(user);
        }
        forgotRepository.delete(forgot);
        forgotRepository.flush();
        String otp = String.format("%06d", new Random().nextInt(100000,999999));
        LocalDateTime expiredDate = LocalDateTime.now().plusMinutes(5);
        Forgot newForgot = new Forgot(otp, user, expiredDate);
        forgotRepository.save(newForgot);
        emailService.sendOtpMail(email, otp);
        model.addAttribute("email", email);
        model.addAttribute("success", "Mã OTP mới đã được gửi!");
        return "verify";
    }

    @PostMapping("/reset")
    public String resetPassword(@RequestParam("email") String email, @RequestParam("password") String newPassword, Model model) {
        userService.updatePassword(email, newPassword);
        model.addAttribute("message", "Mật khẩu đã được thay đổi thành công!");
        return "login";
    }
    
}

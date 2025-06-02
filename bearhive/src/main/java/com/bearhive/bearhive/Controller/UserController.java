package com.bearhive.bearhive.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bearhive.bearhive.Model.User;
import com.bearhive.bearhive.Service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;



@Controller
public class UserController {
    @Autowired 
    UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${recaptcha.site.key}")
    private String recaptchaSiteKey;

    @Value("${recaptcha.secret.key}")
    private String recaptchaSecretKey;

    @Value("${recaptcha.verify.url}")
    private String recaptchaVerifyUrl;
    
    @GetMapping("/signup")
    public String getRegistrationPage(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
        return "signup";
    }
    
    @PostMapping("/signup")
    public String saveUser(@ModelAttribute("user") User user, Model model, 
                            @RequestParam("password") String password, 
                            @RequestParam("g-recaptcha-response") String recaptchaResponse) {
        try {
            // Xác thực reCAPTCHA
            RestTemplate restTemplate = new RestTemplate();
            String url = recaptchaVerifyUrl + "?secret=" + recaptchaSecretKey + "&response=" + recaptchaResponse;
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null) {
                model.addAttribute("message", "Không thể kết nối đến dịch vụ CAPTCHA!");
                model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
                return "signup";
            }

            JsonNode jsonResponse = objectMapper.readTree(response);
            if (!jsonResponse.get("success").asBoolean()) {
                model.addAttribute("message", "Xác thực CAPTCHA thất bại! Lỗi: " + jsonResponse.get("error-codes").toString());
                model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
                return "signup";
            }

            // Kiểm tra đầu vào
            if (user.getEmail() == null || user.getEmail().trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                model.addAttribute("message", "Email và mật khẩu không được để trống!");
                model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
                return "signup";
            }

            User savedUser = userService.registerUser(user, password);
            if (savedUser == null) {
                model.addAttribute("message", "Email đã được đăng kí!");
                model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
                return "signup";
            } else {
                model.addAttribute("message", "Đăng kí thành công!");
                return "redirect:/home"; // Redirect sau khi thành công
            }
        } catch (Exception e) {
            model.addAttribute("message", "Đã xảy ra lỗi: " + e.getMessage());
            model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
            return "signup";
        }
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
        return "login";
    }

    @PostMapping("/login")
    public String loginForm(@RequestParam("email") String email, @RequestParam("password") String password, RedirectAttributes redirectAttributes, HttpSession session, Model model, @RequestParam("g-recaptcha-response") String recaptchaResponse) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = recaptchaVerifyUrl + "?secret=" + recaptchaSecretKey + "&response=" + recaptchaResponse;
            String response = restTemplate.getForObject(url, String.class);
            
            if (response == null || !response.contains("\"success\": true")) {
                redirectAttributes.addFlashAttribute("error", "Xác thực CAPTCHA thất bại!");
                return "redirect:/login";
            }

            User user = userService.loginUser(email, password, session);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Email hoặc mật khẩu không đúng!");
                return "redirect:/login";  
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
            session.setAttribute("loggedUser", user);
            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                session.removeAttribute("redirectAfterLogin"); 
                return "redirect:" + redirectUrl;
            }
            
            return "redirect:/home";   
        } catch (Exception e) {
            model.addAttribute("message", "Đã xảy ra lỗi: " + e.getMessage());
            model.addAttribute("recaptchaSiteKey", recaptchaSiteKey);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/home";  
    }
}

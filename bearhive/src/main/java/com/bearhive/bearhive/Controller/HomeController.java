package com.bearhive.bearhive.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "main";
    }
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home"; 
    }

}

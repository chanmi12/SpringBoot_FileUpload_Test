package com.example.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomController {
    @GetMapping("/")
    public String showHomePage() {
        return "home"; // This will return the home.html page
    }
}

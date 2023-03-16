package com.FALineBot.EndPoint.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/Wishlist")
    public String hello(Model model) {
        model.addAttribute("hello", "我在這裡尋找神秘的大鳳梨"); // （變數名稱，變數值)
        return "model";
    }
}
package main.com.example.schedule_manager.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ObjectMenuController {

    @GetMapping("/object-menu")
    public String objectMenu(HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";
        return "object-menu"; // шаблон object-menu.html
    }
}

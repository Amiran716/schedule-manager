package main.com.example.schedule_manager.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AdminController {

    @Value("${admin.password}")
    private String adminPassword;

    @GetMapping("/admin")
    public String adminLoginPage(Model model) {
        return "admin";
    }

    @PostMapping("/admin")
    public String adminLogin(@RequestParam String password, HttpSession session, Model model) {
        if (adminPassword.equals(password)) {
            session.setAttribute("isAdmin", true);
            return "redirect:/object-menu";
        } else {
            model.addAttribute("errorMessage", "Неверный пароль!");
            return "admin";
        }
    }
}

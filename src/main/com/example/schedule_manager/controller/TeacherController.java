package main.com.example.schedule_manager.controller;


import jakarta.servlet.http.HttpSession;
import main.com.example.schedule_manager.model.Teacher;
import main.com.example.schedule_manager.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teachers")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;

    @GetMapping
    public String showTeachersPage(Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        model.addAttribute("teacher", new Teacher());
        model.addAttribute("teachers", teacherService.getAllTeachersSorted());
        return "teachers";
    }

    @PostMapping("/add")
    public String addTeacher(@ModelAttribute("teacher") @Valid Teacher teacher,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        if (result.hasErrors() || !teacherService.addTeacher(teacher)) {
            model.addAttribute("teachers", teacherService.getAllTeachersSorted());
            model.addAttribute("errorMessage", "Такой преподаватель уже существует или введены некорректные данные.");
            return "teachers";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Преподаватель \"" + teacher.getName() + "\" успешно добавлен!");
        return "redirect:/teachers";
    }

    @PostMapping("/delete")
    public String deleteTeacher(@RequestParam("teacherId") Long id,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        String name = teacherService.getTeacherNameById(id);
        teacherService.deleteTeacherById(id);

        redirectAttributes.addFlashAttribute("successMessageDelete",
                "Преподаватель \"" + name + "\" удален.");
        return "redirect:/teachers";
    }
}
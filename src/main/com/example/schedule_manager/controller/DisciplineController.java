package main.com.example.schedule_manager.controller;

import jakarta.servlet.http.HttpSession;
import main.com.example.schedule_manager.model.Discipline;
import main.com.example.schedule_manager.service.DisciplineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/disciplines")
public class DisciplineController {

    @Autowired
    private DisciplineService disciplineService;

    @GetMapping
    public String showDisciplinesPage(Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";
        model.addAttribute("discipline", new Discipline());
        model.addAttribute("disciplines", disciplineService.getAllDisciplinesSorted());
        return "disciplines";
    }

    @PostMapping("/add")
    public String addDiscipline(@ModelAttribute("discipline") @Valid Discipline discipline,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";
        if (result.hasErrors() || !disciplineService.addDiscipline(discipline)) {
            model.addAttribute("disciplines", disciplineService.getAllDisciplinesSorted());
            model.addAttribute("errorMessage", "Такая дисциплина уже существует или введены некорректные данные.");
            return "disciplines";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Дисциплина \"" + discipline.getName() + "\" успешно добавлена!");
        return "redirect:/disciplines";
    }

    @PostMapping("/delete")
    public String deleteDiscipline(@RequestParam("disciplineId") Long id,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        String name = disciplineService.getDisciplineNameById(id);
        disciplineService.deleteDisciplineById(id);

        redirectAttributes.addFlashAttribute("successMessageDelete",
                "Дисциплина \"" + name + "\" удалена.");

        return "redirect:/disciplines";
    }
}

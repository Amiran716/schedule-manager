package main.com.example.schedule_manager.controller;

import jakarta.servlet.http.HttpSession;
import main.com.example.schedule_manager.model.Group;
import main.com.example.schedule_manager.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping
    public String showGroupsPage(Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";
        model.addAttribute("group", new Group());
        model.addAttribute("groups", groupService.getAllGroupsSorted());
        return "groups";
    }

    @PostMapping("/add")
    public String addGroup(@ModelAttribute("group") @Valid Group group,
                           BindingResult result,
                           @RequestParam(value = "splitIntoSubgroups", required = false) boolean splitIntoSubgroups,
                           Model model,
                           RedirectAttributes redirectAttributes,
                           HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        if (result.hasErrors() || !groupService.addGroup(group, splitIntoSubgroups)) {
            model.addAttribute("groups", groupService.getAllGroupsSorted());
            model.addAttribute("errorMessage",
                    splitIntoSubgroups ?
                            "Группа или её подгруппы уже существуют!" :
                            "Группа уже существует или введены некорректные данные.");
            return "groups";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                splitIntoSubgroups ?
                        "Подгруппы группы " + group.getName() + " успешно добавлены!" :
                        "Группа " + group.getName() + " успешно добавлена!");
        return "redirect:/groups";
    }

    @PostMapping("/delete")
    public String deleteGroup(@RequestParam("groupId") Long groupId,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";
        String groupName = groupService.getGroupNameById(groupId);
        groupService.deleteGroupById(groupId);
        redirectAttributes.addFlashAttribute("successMessageDelete",
                "Группа " + groupName + " удалена.");
        return "redirect:/groups";
    }
}
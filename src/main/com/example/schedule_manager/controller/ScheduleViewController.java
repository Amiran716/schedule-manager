package main.com.example.schedule_manager.controller;

import main.com.example.schedule_manager.model.Group;
import main.com.example.schedule_manager.model.Teacher;
import main.com.example.schedule_manager.repository.GroupRepository;
import main.com.example.schedule_manager.repository.TeacherRepository;
import main.com.example.schedule_manager.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/schedule-view")
@RequiredArgsConstructor
public class ScheduleViewController {

    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final ScheduleService scheduleService; // Добавляем сервис

    @GetMapping
    public String showSelectionPage(@RequestParam(defaultValue = "group") String type,
                                    Model model) {
        model.addAttribute("type", type);

        if ("group".equals(type)) {
            List<Group> groups = groupRepository.findAllByOrderByCourseAscNameAsc();
            model.addAttribute("items", groups);
            model.addAttribute("placeholder", "Начните вводить название группы");
        } else {
            List<Teacher> teachers = teacherRepository.findAllByOrderByNameAsc();
            model.addAttribute("items", teachers);
            model.addAttribute("placeholder", "ФИО преподавателя");
        }

        return "schedule-view";
    }

    @GetMapping("/group")
    public String viewGroupSchedule(
            @RequestParam("id") Long id,  // Явно указываем параметр id
            @RequestParam(defaultValue = "0") short week,
            Model model) {

        Group group = scheduleService.getGroupById(id);
        model.addAttribute("group", group.getName() + " (" + group.getCourse() + " курс)");
        model.addAttribute("week", (int) week);
        model.addAttribute("scheduleData", scheduleService.getGroupSchedule(id));
        model.addAttribute("groupId", id); // для шаблона

        System.out.println("Model week class: " + ((Object) week).getClass().getName());


        return "schedule-group-view";
    }

    // Заменяем существующий метод viewTeacherSchedule:
    @GetMapping("/teacher")
    public String viewTeacherSchedule(
            @RequestParam("id") Long id,
            @RequestParam(defaultValue = "0") short week,
            Model model) {

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Преподаватель не найден"));

        model.addAttribute("teacher", teacher.getName());
        model.addAttribute("week", (int) week);
        model.addAttribute("scheduleData", scheduleService.getTeacherSchedule(id));
        model.addAttribute("teacherId", id);

        return "schedule-teacher-view";
    }
}
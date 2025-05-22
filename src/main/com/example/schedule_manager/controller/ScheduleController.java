package main.com.example.schedule_manager.controller;

import jakarta.servlet.http.HttpSession;
import main.com.example.schedule_manager.dto.ScheduleTableDto;
import main.com.example.schedule_manager.service.ScheduleService;
import main.com.example.schedule_manager.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/schedule-editor")
@RequiredArgsConstructor
public class ScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);
    private final ScheduleService scheduleService;
    private final GroupRepository groupRepository;

    @GetMapping
    public String redirectToEditor(HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        logger.info("Redirecting to default schedule view");
        return "redirect:/schedule-editor/schedule?course=1&week=0&day=0";
    }

    @GetMapping("/schedule")
    public String editor(
            @RequestParam(defaultValue = "1") int course,
            @RequestParam(defaultValue = "0") short week,
            @RequestParam(defaultValue = "0") short day,
            Model model,
            HttpSession session
    ) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        logger.info("Processing schedule request - course: {}, week: {}, day: {}", course, week, day);

        try {
            ScheduleTableDto table = scheduleService.getScheduleTable(course, week, day);
            logger.debug("Retrieved schedule table with {} groups", table.getGroups().size());

            model.addAttribute("table", table);
            model.addAttribute("course", course);
            model.addAttribute("week", week);
            model.addAttribute("day", day);

            logger.info("Successfully processed request, returning schedule-editor view");
            return "schedule-editor";

        } catch (Exception e) {
            logger.error("Error processing schedule request", e);
            throw e;
        }
    }

    @GetMapping("/group-name/{id}")
    @ResponseBody
    public String getGroupName(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        return groupRepository.findById(id)
                .map(g -> g.getName() + " (" + g.getCourse() + " курс)")
                .orElse("Группа не найдена");
    }
}

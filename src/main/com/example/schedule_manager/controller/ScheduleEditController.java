package main.com.example.schedule_manager.controller;

import jakarta.servlet.http.HttpSession;
import main.com.example.schedule_manager.dto.ScheduleAddDto;
import main.com.example.schedule_manager.model.*;
import main.com.example.schedule_manager.repository.*;
import main.com.example.schedule_manager.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleEditController {

    private final ScheduleService scheduleService;
    private final ScheduleRepository scheduleRepository; // Добавлена эта строка
    private final GroupRepository groupRepository;
    private final DisciplineRepository disciplineRepository;
    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;

    @GetMapping("/add")
    public String showAddForm(
            @RequestParam int groupId,
            @RequestParam int lessonNumber,
            @RequestParam int day,
            @RequestParam int week,
            @RequestParam int course,
            Model model,
            HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        Group group = groupRepository.findById((long) groupId)
                .orElseThrow(() -> new IllegalArgumentException("Группа не найдена"));

        // Создаем или обновляем DTO
        ScheduleAddDto scheduleAdd = new ScheduleAddDto();
        scheduleAdd.setGroupId(groupId);
        scheduleAdd.setLessonNumber(lessonNumber);
        scheduleAdd.setDay(day);
        scheduleAdd.setWeek(week);
        scheduleAdd.setCourse(course);

        // Если есть сообщение об ошибке - сохраняем остальные поля из модели
        if (model.containsAttribute("errorMessage")) {
            ScheduleAddDto errorDto = (ScheduleAddDto) model.getAttribute("scheduleAdd");
            if (errorDto != null) {
                scheduleAdd.setDisciplineId(errorDto.getDisciplineId());
                scheduleAdd.setTeacherId(errorDto.getTeacherId());
                scheduleAdd.setRoomId(errorDto.getRoomId());
                scheduleAdd.setType(errorDto.getType());
                scheduleAdd.setCombined(errorDto.isCombined());
            }
        }

        prepareModel(model, group, scheduleAdd, false);
        return "schedule-add";
    }

    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam Long id,
            @RequestParam int course,
            @RequestParam short week,
            @RequestParam short day,
            Model model,
            HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Занятие не найдено"));

        ScheduleAddDto dto = new ScheduleAddDto();
        dto.setScheduleId(schedule.getId());
        dto.setGroupId(schedule.getGroup().getId().intValue());
        dto.setLessonNumber(schedule.getLessonNumber());
        dto.setDay(schedule.getDay());
        dto.setWeek(schedule.getWeek());
        dto.setCourse(course);
        dto.setDisciplineId(schedule.getDiscipline().getId());
        dto.setTeacherId(schedule.getTeacher().getId());
        dto.setRoomId(schedule.getRoom().getId());
        dto.setType(schedule.getType());

        prepareModel(model, schedule.getGroup(), dto, true);
        return "schedule-add";
    }

    @PostMapping("/add")
    public String addSchedule(
            @ModelAttribute ScheduleAddDto scheduleAdd,
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        if (session.getAttribute("isAdmin") == null) return "access-denied";

        try {
            scheduleService.addSchedule(scheduleAdd);
            redirectAttributes.addFlashAttribute("successMessage", "Занятие успешно добавлено!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Добавляем все параметры в редирект
            redirectAttributes.addAttribute("groupId", scheduleAdd.getGroupId());
            redirectAttributes.addAttribute("lessonNumber", scheduleAdd.getLessonNumber());
            redirectAttributes.addAttribute("day", scheduleAdd.getDay());
            redirectAttributes.addAttribute("week", scheduleAdd.getWeek());
            redirectAttributes.addAttribute("course", scheduleAdd.getCourse()); // Добавляем курс
            return "redirect:/schedule/add";
        }

        return "redirect:/schedule-editor/schedule?" + scheduleAdd.toRedirectParams();
    }

    @PostMapping("/edit")
    public String updateSchedule(
            @RequestParam Long id,
            @ModelAttribute ScheduleAddDto scheduleAdd,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        try {
            scheduleService.updateSchedule(id, scheduleAdd);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/schedule/edit?id=" + id + "&" + scheduleAdd.toRedirectParams();
        }

        return "redirect:/schedule-editor/schedule?" + scheduleAdd.toRedirectParams();
    }

    @PostMapping("/delete")
    public String deleteSchedule(
            @RequestParam Long id,
            @RequestParam int course,
            @RequestParam short week,
            @RequestParam short day,
            HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        scheduleService.deleteSchedule(id);
        return "redirect:/schedule-editor/schedule?course=" + course + "&week=" + week + "&day=" + day;
    }

    private void prepareModel(Model model, Group group, ScheduleAddDto scheduleAdd, boolean isEdit) {
        model.addAttribute("groupName", group.getName() + " (" + group.getCourse() + " курс)");
        model.addAttribute("dayNames", new String[]{"Пн", "Вт", "Ср", "Чт", "Пт", "Сб"});
        model.addAttribute("scheduleAdd", scheduleAdd);
        model.addAttribute("isEdit", isEdit);
        model.addAttribute("disciplines", disciplineRepository.findAllByOrderByNameAsc());
        model.addAttribute("teachers", teacherRepository.findAllByOrderByNameAsc());
        model.addAttribute("rooms", roomRepository.findAllByOrderByNameAsc());
    }
}
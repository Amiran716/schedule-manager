package main.com.example.schedule_manager.controller;

import jakarta.servlet.http.HttpSession;
import main.com.example.schedule_manager.model.Room;
import main.com.example.schedule_manager.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping
    public String showRoomsPage(Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        model.addAttribute("room", new Room());
        model.addAttribute("rooms", roomService.getAllRoomsSorted());
        return "rooms";
    }

    @PostMapping("/add")
    public String addRoom(@ModelAttribute("room") @Valid Room room,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes,
                          HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";

        if (result.hasErrors() || !roomService.addRoom(room)) {
            model.addAttribute("rooms", roomService.getAllRoomsSorted());
            model.addAttribute("errorMessage", "Такая аудитория уже существует или введены некорректные данные.");
            return "rooms";
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Аудитория \"" + room.getName() + "\" успешно добавлена!");
        return "redirect:/rooms";
    }

    @PostMapping("/delete")
    public String deleteRoom(@RequestParam("roomId") Long id,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "access-denied";


        String name = roomService.getRoomNameById(id);
        roomService.deleteRoomById(id);

        redirectAttributes.addFlashAttribute("successMessageDelete",
                "Аудитория \"" + name + "\" удалена.");
        return "redirect:/rooms";
    }
}

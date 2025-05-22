package main.com.example.schedule_manager.service;

import main.com.example.schedule_manager.model.Room;
import main.com.example.schedule_manager.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    public boolean addRoom(Room room) {
        if (room.getName() == null || room.getName().trim().isEmpty()) {
            return false;
        }

        if (roomRepository.findByName(room.getName()).isPresent()) {
            return false; // Дубликат
        }

        try {
            roomRepository.save(room);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteRoomById(Long id) {
        roomRepository.deleteById(id);
    }

    public List<Room> getAllRoomsSorted() {
        return roomRepository.findAllByOrderByNameAsc();
    }

    public String getRoomNameById(Long id) {
        return roomRepository.findById(id)
                .map(Room::getName)
                .orElse("");
    }
}

package main.com.example.schedule_manager.repository;

import main.com.example.schedule_manager.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByName(String name);
    List<Room> findAllByOrderByNameAsc();
}

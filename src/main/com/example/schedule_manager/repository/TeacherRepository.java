package main.com.example.schedule_manager.repository;

import main.com.example.schedule_manager.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByName(String name);
    List<Teacher> findAllByOrderByNameAsc();
}

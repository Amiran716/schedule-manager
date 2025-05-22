package main.com.example.schedule_manager.repository;

import main.com.example.schedule_manager.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface DisciplineRepository extends JpaRepository<Discipline, Long> {
    Optional<Discipline> findByName(String name);
    List<Discipline> findAllByOrderByNameAsc();
}

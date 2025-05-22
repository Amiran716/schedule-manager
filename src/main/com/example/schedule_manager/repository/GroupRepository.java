package main.com.example.schedule_manager.repository;

import main.com.example.schedule_manager.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByNameAndCourse(String name, Integer course);

    List<Group> findAllByOrderByCourseAscNameAsc();

    List<Group> findByCourseOrderByNameAsc(int course);
}

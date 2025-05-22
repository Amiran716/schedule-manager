package main.com.example.schedule_manager.service;

import main.com.example.schedule_manager.model.Teacher;
import main.com.example.schedule_manager.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    public boolean addTeacher(Teacher teacher) {
        if (teacher.getName() == null || teacher.getName().trim().isEmpty()) {
            return false;
        }

        if (teacherRepository.findByName(teacher.getName()).isPresent()) {
            return false; // Дубликат
        }

        try {
            teacherRepository.save(teacher);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteTeacherById(Long id) {
        teacherRepository.deleteById(id);
    }

    public List<Teacher> getAllTeachersSorted() {
        return teacherRepository.findAllByOrderByNameAsc();
    }

    public String getTeacherNameById(Long id) {
        return teacherRepository.findById(id)
                .map(Teacher::getName)
                .orElse("");
    }
}

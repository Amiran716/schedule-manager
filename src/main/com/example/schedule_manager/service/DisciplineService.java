package main.com.example.schedule_manager.service;

import main.com.example.schedule_manager.model.Discipline;
import main.com.example.schedule_manager.repository.DisciplineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisciplineService {

    @Autowired
    private DisciplineRepository disciplineRepository;

    public boolean addDiscipline(Discipline discipline) {
        if (discipline.getName() == null || discipline.getName().trim().isEmpty()) {
            return false;
        }

        String normalized = discipline.getName().trim();

        if (disciplineRepository.findByName(normalized).isPresent()) {
            return false; // дубликат
        }

        discipline.setName(normalized);

        try {
            disciplineRepository.save(discipline);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteDisciplineById(Long id) {
        disciplineRepository.deleteById(id);
    }

    public List<Discipline> getAllDisciplinesSorted() {
        return disciplineRepository.findAllByOrderByNameAsc();
    }

    public String getDisciplineNameById(Long id) {
        return disciplineRepository.findById(id)
                .map(Discipline::getName)
                .orElse("");
    }
}

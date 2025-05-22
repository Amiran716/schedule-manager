package main.com.example.schedule_manager.service;

import main.com.example.schedule_manager.model.Group;
import main.com.example.schedule_manager.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Transactional
    public boolean addGroup(Group group, boolean splitIntoSubgroups) {
        if (group.getName() == null || group.getName().trim().isEmpty()) {
            return false;
        }

        String normalizedName = group.getName().trim();
        group.setName(normalizedName);

        // Проверка основной группы
        if (groupRepository.findByNameAndCourse(normalizedName, group.getCourse()).isPresent()) {
            return false;
        }

        // Проверка подгрупп (если нужно)
        if (splitIntoSubgroups) {
            if (groupRepository.findByNameAndCourse(normalizedName + " (1 подгр.)", group.getCourse()).isPresent() ||
                    groupRepository.findByNameAndCourse(normalizedName + " (2 подгр.)", group.getCourse()).isPresent()) {
                return false;
            }
        }

        try {
            if (splitIntoSubgroups) {
                // Создаём две подгруппы
                Group subgroup1 = new Group();
                subgroup1.setName(normalizedName + " (1 подгр.)");
                subgroup1.setCourse(group.getCourse());
                subgroup1.setSubgroup(true);

                Group subgroup2 = new Group();
                subgroup2.setName(normalizedName + " (2 подгр.)");
                subgroup2.setCourse(group.getCourse());
                subgroup2.setSubgroup(true);

                groupRepository.save(subgroup1);
                groupRepository.save(subgroup2);
            } else {
                groupRepository.save(group);
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении группы", e);
        }
    }

    public void deleteGroupById(Long id) {
        groupRepository.deleteById(id);
    }

    public List<Group> getAllGroupsSorted() {
        return groupRepository.findAllByOrderByCourseAscNameAsc();
    }

    public String getGroupNameById(Long id) {
        return groupRepository.findById(id)
                .map(Group::getName)
                .orElse("");
    }
}
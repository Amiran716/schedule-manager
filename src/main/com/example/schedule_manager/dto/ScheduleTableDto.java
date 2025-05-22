package main.com.example.schedule_manager.dto;

import main.com.example.schedule_manager.model.Group;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class ScheduleTableDto {
    private List<Group> groups;

    private Map<Integer, Map<Long, LessonCellDto>> cellsByLessonAndGroup; // заменил тип Short на Integer, в бд сделал тоже самое
}

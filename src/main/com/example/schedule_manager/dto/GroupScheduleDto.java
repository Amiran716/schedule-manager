package main.com.example.schedule_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupScheduleDto {
    private Integer lessonNumber;
    private String disciplineWithType;
    private String room;
    private String teacher;
}
package main.com.example.schedule_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonCellDto {
    private Long scheduleId;
    private String discipline;
    private String type;
    private String room;
    private String teacher;
    private boolean empty;
    private Long groupId;
    private int lessonNumber;
    private short day;
    private short week;
}

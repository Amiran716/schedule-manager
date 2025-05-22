package main.com.example.schedule_manager.dto;

import lombok.Data;

@Data
public class ScheduleAddDto {
    private Long scheduleId; // поле для ID редактируемой записи
    private int groupId;
    private int lessonNumber;
    private int day; // Изменено с short на int
    private int week; // Изменено с short на int
    private int course;
    private Long disciplineId; // Изменено с int на Long
    private Long teacherId; // Изменено с int на Long
    private Long roomId; // Изменено с int на Long
    private String type;
    private boolean combined;

    public String toQueryParams() {
        return String.format("groupId=%d&lessonNumber=%d&day=%d&week=%d",
                groupId, lessonNumber, day, week);
    }

    public String toRedirectParams() {
        return String.format("course=%d&week=%d&day=%d", course, week, day); // Используем сохраненный курс
    }
}
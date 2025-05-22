package main.com.example.schedule_manager.repository;

import main.com.example.schedule_manager.model.Schedule;
import main.com.example.schedule_manager.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByGroupInAndWeekAndDayOrderByLessonNumberAsc(List<Group> groups, short week, short day);

    @Query("SELECT s FROM Schedule s WHERE " +
            "s.day = :day AND s.week = :week AND " +
            "s.lessonNumber = :lessonNumber AND s.room.id = :roomId")
    List<Schedule> findAllByDayAndWeekAndLessonNumberAndRoomId(
            @Param("day") short day,
            @Param("week") short week,
            @Param("lessonNumber") int lessonNumber,
            @Param("roomId") Long roomId
    );

    @Query("SELECT s FROM Schedule s WHERE " +
            "s.day = :day AND s.week = :week AND " +
            "s.lessonNumber = :lessonNumber AND s.teacher.id = :teacherId")
    List<Schedule> findAllByDayAndWeekAndLessonNumberAndTeacherId(
            @Param("day") short day,
            @Param("week") short week,
            @Param("lessonNumber") int lessonNumber,
            @Param("teacherId") Long teacherId
    );

    @Query("SELECT s FROM Schedule s WHERE " +
            "s.day = :day AND s.week = :week AND " +
            "s.lessonNumber = :lessonNumber AND s.room.id = :roomId AND " +
            "s.id != :excludeId")
    List<Schedule> findAllByDayAndWeekAndLessonNumberAndRoomIdAndIdNot(
            @Param("day") short day,
            @Param("week") short week,
            @Param("lessonNumber") int lessonNumber,
            @Param("roomId") Long roomId,
            @Param("excludeId") Long excludeId
    );

    @Query("SELECT s FROM Schedule s WHERE " +
            "s.day = :day AND s.week = :week AND " +
            "s.lessonNumber = :lessonNumber AND s.teacher.id = :teacherId AND " +
            "s.id != :excludeId")
    List<Schedule> findAllByDayAndWeekAndLessonNumberAndTeacherIdAndIdNot(
            @Param("day") short day,
            @Param("week") short week,
            @Param("lessonNumber") int lessonNumber,
            @Param("teacherId") Long teacherId,
            @Param("excludeId") Long excludeId
    );

    @Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.discipline " +
            "JOIN FETCH s.teacher " +
            "JOIN FETCH s.room " +
            "WHERE s.group.id = :groupId " +
            "ORDER BY s.week, s.day, s.lessonNumber")
    List<Schedule> findFullScheduleByGroupId(Long groupId);

    @Query("SELECT s FROM Schedule s " +
            "JOIN FETCH s.discipline " +
            "JOIN FETCH s.room " +
            "JOIN FETCH s.group " +
            "WHERE s.teacher.id = :teacherId " +
            "ORDER BY s.week, s.day, s.lessonNumber")
    List<Schedule> findFullScheduleByTeacherId(Long teacherId);
}
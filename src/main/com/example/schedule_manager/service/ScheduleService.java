package main.com.example.schedule_manager.service;

import main.com.example.schedule_manager.dto.*;
import main.com.example.schedule_manager.model.*;
import main.com.example.schedule_manager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final GroupRepository groupRepository;
    private final DisciplineRepository disciplineRepository;
    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;

    public ScheduleTableDto getScheduleTable(int course, short week, short day) {
        List<Group> groups = groupRepository.findByCourseOrderByNameAsc(course);
        if (groups.isEmpty()) return new ScheduleTableDto(Collections.emptyList(), Collections.emptyMap());

        List<Schedule> schedules = scheduleRepository.findByGroupInAndWeekAndDayOrderByLessonNumberAsc(groups, week, day);
        Map<Integer, Map<Long, LessonCellDto>> cells = new HashMap<>();

        for (int lessonNumber = 0; lessonNumber < 8; lessonNumber++) {
            final int currentLessonNumber = lessonNumber;
            Map<Long, LessonCellDto> row = new HashMap<>();

            for (Group group : groups) {
                Optional<Schedule> opt = schedules.stream()
                        .filter(s -> s.getLessonNumber() == currentLessonNumber
                                && s.getGroup().getId().equals(group.getId()))
                        .findFirst();

                LessonCellDto dto = opt.map(s -> new LessonCellDto(
                                s.getId(),
                                s.getDiscipline().getName(),
                                s.getType(),
                                s.getRoom().getName(),
                                s.getTeacher().getName(),
                                false,
                                group.getId(),
                                currentLessonNumber,
                                day,
                                week
                        ))
                        .orElseGet(() -> new LessonCellDto(
                                null, null, null, null, null,
                                true,
                                group.getId(),
                                currentLessonNumber,
                                day,
                                week
                        ));

                row.put(group.getId(), dto);
            }
            cells.put(lessonNumber, row);
        }
        return new ScheduleTableDto(groups, cells);
    }

    @Transactional
    public void addSchedule(ScheduleAddDto scheduleAdd) {
        if (!scheduleAdd.isCombined()) {
            checkConflicts(scheduleAdd);
        }

        Schedule schedule = new Schedule();
        schedule.setDay((short) scheduleAdd.getDay());
        schedule.setWeek((short) scheduleAdd.getWeek());
        schedule.setLessonNumber(scheduleAdd.getLessonNumber());
        schedule.setGroup(groupRepository.findById((long) scheduleAdd.getGroupId()).orElseThrow());
        schedule.setDiscipline(disciplineRepository.findById(scheduleAdd.getDisciplineId()).orElseThrow());
        schedule.setTeacher(teacherRepository.findById(scheduleAdd.getTeacherId()).orElseThrow());
        schedule.setRoom(roomRepository.findById(scheduleAdd.getRoomId()).orElseThrow());
        schedule.setType(scheduleAdd.getType());

        scheduleRepository.save(schedule);
    }

    @Transactional
    public void updateSchedule(Long id, ScheduleAddDto dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Занятие не найдено"));

        if (!dto.isCombined() &&
                (!dto.getRoomId().equals(schedule.getRoom().getId()) ||
                        !dto.getTeacherId().equals(schedule.getTeacher().getId()))) {
            checkConflictsForUpdate(id, dto);
        }

        schedule.setDiscipline(disciplineRepository.findById(dto.getDisciplineId()).orElseThrow());
        schedule.setTeacher(teacherRepository.findById(dto.getTeacherId()).orElseThrow());
        schedule.setRoom(roomRepository.findById(dto.getRoomId()).orElseThrow());
        schedule.setType(dto.getType());

        scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }

    private void checkConflicts(ScheduleAddDto scheduleAdd) {
        List<Schedule> roomConflicts = scheduleRepository
                .findAllByDayAndWeekAndLessonNumberAndRoomId(
                        (short) scheduleAdd.getDay(),
                        (short) scheduleAdd.getWeek(),
                        scheduleAdd.getLessonNumber(),
                        scheduleAdd.getRoomId());

        List<Schedule> teacherConflicts = scheduleRepository
                .findAllByDayAndWeekAndLessonNumberAndTeacherId(
                        (short) scheduleAdd.getDay(),
                        (short) scheduleAdd.getWeek(),
                        scheduleAdd.getLessonNumber(),
                        scheduleAdd.getTeacherId());

        handleConflicts(roomConflicts, teacherConflicts);
    }

    private void checkConflictsForUpdate(Long scheduleId, ScheduleAddDto dto) {
        List<Schedule> roomConflicts = scheduleRepository
                .findAllByDayAndWeekAndLessonNumberAndRoomIdAndIdNot(
                        (short) dto.getDay(),
                        (short) dto.getWeek(),
                        dto.getLessonNumber(),
                        dto.getRoomId(),
                        scheduleId);

        List<Schedule> teacherConflicts = scheduleRepository
                .findAllByDayAndWeekAndLessonNumberAndTeacherIdAndIdNot(
                        (short) dto.getDay(),
                        (short) dto.getWeek(),
                        dto.getLessonNumber(),
                        dto.getTeacherId(),
                        scheduleId);

        handleConflicts(roomConflicts, teacherConflicts);
    }

    private void handleConflicts(List<Schedule> roomConflicts, List<Schedule> teacherConflicts) {
        StringBuilder errorMessage = new StringBuilder();

        if (!roomConflicts.isEmpty()) {
            errorMessage.append("Аудитория занята: ")
                    .append(roomConflicts.stream()
                            .map(s -> s.getGroup().getCourse() + " курс " + s.getGroup().getName())
                            .collect(Collectors.joining(", ")))
                    .append("\n\n");
        }
        // аналогичный блок для преподавателя
        if (!teacherConflicts.isEmpty()) {
            errorMessage.append("Преподаватель занят: ")
                    .append(teacherConflicts.stream()
                            .map(s -> s.getGroup().getCourse() + " курс " + s.getGroup().getName())
                            .collect(Collectors.joining(", ")));
        }

        if (errorMessage.length() > 0) {
            throw new RuntimeException(errorMessage.toString().trim());
        }
    }

    public Map<Integer, Map<Integer, Map<Integer, GroupScheduleDto>>> getGroupSchedule(Long groupId) {
        List<Schedule> schedules = scheduleRepository.findFullScheduleByGroupId(groupId);

        Map<Integer, Map<Integer, Map<Integer, GroupScheduleDto>>> result = new HashMap<>();

        for (Schedule s : schedules) {
            int week = s.getWeek();
            int day = s.getDay();
            int lesson = s.getLessonNumber();

            result
                    .computeIfAbsent(week, k -> new HashMap<>())
                    .computeIfAbsent(day, k -> new HashMap<>())
                    .put(lesson, new GroupScheduleDto(
                            lesson,
                            s.getDiscipline().getName() + "\n(" + s.getType() + ")",
                            s.getRoom().getName(),
                            s.getTeacher().getName()
                    ));
        }

        return result;
    }

    public Map<Integer, Map<Integer, Map<Integer, TeacherScheduleDto>>> getTeacherSchedule(Long teacherId) {
        List<Schedule> schedules = scheduleRepository.findFullScheduleByTeacherId(teacherId);

        Map<Integer, Map<Integer, Map<Integer, TeacherScheduleDto>>> result = new HashMap<>();

        for (Schedule s : schedules) {
            int week = s.getWeek();
            int day = s.getDay();
            int lesson = s.getLessonNumber();

            // Формируем строку с группами
            String groups = s.getGroup().getName() + " (" + s.getGroup().getCourse() + " курс)";

            result
                    .computeIfAbsent(week, k -> new HashMap<>())
                    .computeIfAbsent(day, k -> new HashMap<>())
                    .merge(lesson,
                            new TeacherScheduleDto(
                                    lesson,
                                    s.getDiscipline().getName() + "\n(" + s.getType() + ")",
                                    s.getRoom().getName(),
                                    groups
                            ),
                            (existing, newVal) -> {
                                // Объединяем группы через перенос строки
                                existing.setGroups(existing.getGroups() + "\n" + newVal.getGroups());
                                return existing;
                            });
        }

        return result;
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Группа не найдена"));
    }
    
}
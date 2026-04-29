package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final LessonRepository lessonRepository;
    private final CourseGroupRepository groupRepository;

    @Transactional(readOnly = true)
    public long getStudentCount() {
        return studentRepository.count();
    }

    @Transactional(readOnly = true)
    public long getTeacherCount() {
        return teacherRepository.count();
    }

    @Transactional(readOnly = true)
    public long getLessonCount() {
        return lessonRepository.count();
    }

    @Transactional(readOnly = true)
    public long getGroupCount() {
        return groupRepository.count();
    }
}
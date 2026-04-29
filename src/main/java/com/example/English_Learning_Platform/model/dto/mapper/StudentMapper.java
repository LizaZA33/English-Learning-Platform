package com.example.English_Learning_Platform.model.dto.mapper;

import com.example.English_Learning_Platform.model.dto.response.CourseGroupResponse;
import com.example.English_Learning_Platform.model.dto.response.LectureProgressResponse;
import com.example.English_Learning_Platform.model.dto.response.LessonProgressResponse;
import com.example.English_Learning_Platform.model.dto.response.StudentResponse;
import com.example.English_Learning_Platform.model.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    @Mapping(target = "email", expression = "java(student.getUserEntity().getEmail())")
    @Mapping(target = "groups", source = "studentGroupEntities", qualifiedByName = "mapGroups")
    @Mapping(target = "lectureProgress", source = "lectureProgress", qualifiedByName = "mapLectureProgress")
    @Mapping(target = "lessonProgress", source = "lessonProgress", qualifiedByName = "mapLessonProgress")
    StudentResponse toResponse(StudentEntity student);

    List<StudentResponse> toResponseList(List<StudentEntity> studentEntities);

    @Named("mapGroups")
    default List<CourseGroupResponse> mapGroups(Set<StudentGroupEntity> studentGroupEntities) {
        if (studentGroupEntities == null) return List.of();
        return studentGroupEntities.stream()
                .map(sg -> {
                    CourseGroupEntity group = sg.getGroup();
                    return CourseGroupResponse.builder()
                            .id(group.getId())
                            .name(group.getName())
                            .inviteCode(group.getInviteCode())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Named("mapLectureProgress")
    default List<LectureProgressResponse> mapLectureProgress(Set<LectureProgressStudentEntity> progress) {
        if (progress == null) return List.of();
        return progress.stream()
                .map(p -> LectureProgressResponse.builder()
                        .lectureId(p.getLectureEntity().getId())
                        .lectureTitle(p.getLectureEntity().getTitle())
                        .lectureProgress(p.getProgressPercent())
                        .build())
                .collect(Collectors.toList());
    }

    @Named("mapLessonProgress")
    default List<LessonProgressResponse> mapLessonProgress(Set<LessonProgressStudentEntity> progress) {
        if (progress == null) return List.of();
        return progress.stream()
                .map(p -> LessonProgressResponse.builder()
                        .lessonId(p.getLessonEntity().getId())
                        .lessonTitle(p.getLessonEntity().getTitle())
                        .lessonProgress(p.getProgressPercent())
                        .build())
                .collect(Collectors.toList());
    }
}
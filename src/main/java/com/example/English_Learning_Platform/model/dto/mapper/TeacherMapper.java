package com.example.English_Learning_Platform.model.dto.mapper;

import com.example.English_Learning_Platform.model.dto.response.CourseGroupResponse;
import com.example.English_Learning_Platform.model.dto.response.TeacherResponse;
import com.example.English_Learning_Platform.model.entity.CourseGroupEntity;
import com.example.English_Learning_Platform.model.entity.TeacherEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

    @Mapping(target = "email", expression = "java(teacherEntity.getUserEntity().getEmail())")
    @Mapping(target = "groups", source = "groups", qualifiedByName = "mapGroups")
    TeacherResponse toResponse(TeacherEntity teacherEntity);

    List<TeacherResponse> toResponseList(List<TeacherEntity> teacherEntities);

    @Named("mapGroups")
    default List<CourseGroupResponse> mapGroups(Set<CourseGroupEntity> groups) {
        if (groups == null) return List.of();
        return groups.stream()
                .map(group -> CourseGroupResponse.builder()
                        .id(group.getId())
                        .name(group.getName())
                        .inviteCode(group.getInviteCode())
                        .studentCount(group.getStudentGroupEntities() != null ? group.getStudentGroupEntities().size() : 0)
                        .build())
                .collect(Collectors.toList());
    }
}

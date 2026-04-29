package com.example.English_Learning_Platform.model.dto.mapper;

import com.example.English_Learning_Platform.model.dto.request.CourseGroupRequest;
import com.example.English_Learning_Platform.model.dto.response.CourseGroupResponse;
import com.example.English_Learning_Platform.model.dto.response.ModuleResponse;
import com.example.English_Learning_Platform.model.dto.response.TeacherResponse;
import com.example.English_Learning_Platform.model.entity.CourseGroupEntity;
import com.example.English_Learning_Platform.model.entity.ModuleEntity;
import com.example.English_Learning_Platform.model.entity.TeacherEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseGroupMapper {

    @Mapping(target = "teacher", source = "teacherEntity", qualifiedByName = "mapTeacher")
    @Mapping(target = "module", source = "moduleEntity", qualifiedByName = "mapModule")
    @Mapping(target = "studentCount", expression = "java(group.getStudentGroupEntities() != null ? group.getStudentGroupEntities().size() : 0)")
    CourseGroupResponse toResponse(CourseGroupEntity group);

    List<CourseGroupResponse> toResponseList(List<CourseGroupEntity> groups);

    @Named("mapTeacher")
    default TeacherResponse mapTeacher(TeacherEntity teacherEntity) {
        if (teacherEntity == null) return null;
        return TeacherResponse.builder()
                .id(teacherEntity.getId())
                .firstName(teacherEntity.getFirstName())
                .lastName(teacherEntity.getLastName())
                .patronymic(teacherEntity.getPatronymic())
                .email(teacherEntity.getUserEntity() != null ? teacherEntity.getUserEntity().getEmail() : null)
                .phoneNumber(teacherEntity.getPhoneNumber())
                .build();
    }

    @Named("mapModule")
    default ModuleResponse mapModule(ModuleEntity moduleEntity) {
        if (moduleEntity == null) return null;
        return ModuleResponse.builder()
                .id(moduleEntity.getId())
                .name(moduleEntity.getName())
                .build();
    }
}
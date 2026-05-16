package com.example.English_Learning_Platform.model.dto.mapper;

import com.example.English_Learning_Platform.model.dto.response.UserResponse;
import com.example.English_Learning_Platform.model.entity.StudentEntity;
import com.example.English_Learning_Platform.model.entity.TeacherEntity;
import com.example.English_Learning_Platform.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "teacher", source = "teacherEntity", qualifiedByName = "mapTeacherInfo")
    @Mapping(target = "student", source = "studentEntity", qualifiedByName = "mapStudentInfo")
    UserResponse toResponse(UserEntity userEntity);

    @Named("mapTeacherInfo")
    default UserResponse.TeacherInfo mapTeacherInfo(TeacherEntity teacherEntity) {
        if (teacherEntity == null) return null;
        return UserResponse.TeacherInfo.builder()
                .id(teacherEntity.getId())
                .firstName(teacherEntity.getFirstName())
                .lastName(teacherEntity.getLastName())
                .patronymic(teacherEntity.getPatronymic())
                .build();
    }

    @Named("mapStudentInfo")
    default UserResponse.StudentInfo mapStudentInfo(StudentEntity studentEntity) {
        if (studentEntity == null) return null;
        return UserResponse.StudentInfo.builder()
                .id(studentEntity.getId())
                .firstName(studentEntity.getFirstName())
                .lastName(studentEntity.getLastName())
                .patronymic(studentEntity.getPatronymic())
                .build();
    }
}

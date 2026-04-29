package com.example.English_Learning_Platform.model.dto.response;

import com.example.English_Learning_Platform.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private Set<Role> roles;
    private TeacherInfo teacher;
    private StudentInfo student;
    @Data
    @Builder
    public static class TeacherInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String patronymic;
    }
    @Data
    @Builder
    public static class StudentInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String patronymic;
    }
}

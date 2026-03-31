package com.example.English_Learning_Platform.model.dto.response;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String email;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private List<CourseGroupResponse> groups;
    private Integer totalLectures;
    private Integer completedLectures;
    private Integer totalLessons;
    private Integer completedLessons;
}
package com.example.English_Learning_Platform.model.dto.response;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseGroupResponse {
    private Long id;
    private String name;
    private String inviteCode;
    private TeacherResponse teacher;
    private ModuleResponse module;
    private Integer studentCount;
}

package com.example.English_Learning_Platform.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonCreateRequest {
    @NotBlank(message = "Название урока обязательно")
    private String title;
    private String description;
    private Long teacherId;
}
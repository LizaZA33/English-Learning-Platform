package com.example.English_Learning_Platform.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressResponse {
    private Long lessonId;
    private String lessonTitle;
    private Integer lessonProgress;
}

package com.example.English_Learning_Platform.model.dto.response;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {
    private Long id;
    private String title;
    private String description;
    private TeacherResponse teacher;
    private List<FlashcardResponse> flashcards;
}

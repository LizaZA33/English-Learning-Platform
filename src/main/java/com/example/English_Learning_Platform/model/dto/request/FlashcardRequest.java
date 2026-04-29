package com.example.English_Learning_Platform.model.dto.request;

import com.example.English_Learning_Platform.model.entity.LessonEntity;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardRequest {
    @NotNull(message = "Требуется ID занятия")
    private Long lessonId;
    @NotBlank(message = "Требуется термин")
    @Size(min = 1, max = 500, message = "Термин должен содержать от 1 до 500 символов")
    private String term;
    @Size(max = 2000, message = "Определение не может превышать 2000 символов")
    private String definition;
    @Size(max = 2000, message = "Пример не может превышать 2000 символов")
    private String example;
    @NotBlank(message = "Требуется перевод")
    @Size(min = 1, max = 500, message = "Перевод должен содержать от 1 до 500 символов")
    private String translation;
    @Min(1)
    @Max(5)
    private Integer difficulty = 1;
}

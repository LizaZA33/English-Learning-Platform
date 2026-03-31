package com.example.English_Learning_Platform.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardRequest {
    @NotNull(message = "Требуеться название занятия")
    private Long lessonName;
    @NotBlank(message = "Требуеться термин")
    @Size(min = 1, max = 500, message = "Кол-во символов в названии должно быть от 1 до 500")
    private String term;
    @Size(max = 500, message = "Кол-во символов в названии должно быть до 500")
    private String definition;
    @Size(max = 500, message = "Кол-во символов в названии должно быть от 2 до 500")
    private String example;
    @NotBlank(message = "Требуеться перервод")
    @Size(min = 1, max = 500, message = "Кол-во символов в названии должно быть от 1 до 500")
    private String translation;
    private Integer difficulty = 1;
}

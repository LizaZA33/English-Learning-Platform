package com.example.English_Learning_Platform.model.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardResponse {
    private Long id;
    private String term;
    private String definition;
    private String example;
    private String translation;
    private Integer difficulty;
    private Long lessonId;
}


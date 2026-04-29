package com.example.English_Learning_Platform.model.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureResponse {
    private Long id;
    private String title;
    private String content;
    private ModuleResponse module;
}

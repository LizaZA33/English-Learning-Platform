package com.example.English_Learning_Platform.model.dto.response;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureProgressResponse {
    private Long lectureId;
    private String lectureTitle;
    private Integer progressPercent;
}

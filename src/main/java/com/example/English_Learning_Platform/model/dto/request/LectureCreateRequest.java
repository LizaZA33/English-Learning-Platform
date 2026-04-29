package com.example.English_Learning_Platform.model.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LectureCreateRequest {
    @NotBlank(message = "Название лекции обязательно")
    private String title;
    private String content;
    @NotNull(message = "Наименование уровня обязательно")
    private String moduleName;
}

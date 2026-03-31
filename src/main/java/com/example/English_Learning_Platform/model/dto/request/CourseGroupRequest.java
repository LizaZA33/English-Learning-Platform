package com.example.English_Learning_Platform.model.dto.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseGroupRequest {
    @NotBlank(message = "Требуеться наименование группы")
    @Size(min = 3, max = 100, message = "Кол-во символов в названии должно быть от 2 до 100")
    private String groupName;
    @NotNull(message = "Требуеться ФИО учителя")
    private Long teacherFullName;
    @NotNull(message = "Требуеться модуль")
    private String moduleName;
}

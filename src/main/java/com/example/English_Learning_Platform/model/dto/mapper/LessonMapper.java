package com.example.English_Learning_Platform.model.dto.mapper;

import com.example.English_Learning_Platform.model.dto.response.FlashcardResponse;
import com.example.English_Learning_Platform.model.dto.response.LessonResponse;
import com.example.English_Learning_Platform.model.dto.response.TeacherResponse;
import com.example.English_Learning_Platform.model.entity.FlashcardEntity;
import com.example.English_Learning_Platform.model.entity.LessonEntity;
import com.example.English_Learning_Platform.model.entity.TeacherEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    @Mapping(target = "teacher", source = "teacherEntity", qualifiedByName = "mapTeacher")
    @Mapping(target = "flashcards", source = "flashcardEntities", qualifiedByName = "mapFlashcards")
    LessonResponse toResponse(LessonEntity lessonEntity);

    List<LessonResponse> toResponseList(List<LessonEntity> lessonEntities);

    @Named("mapTeacher")
    default TeacherResponse mapTeacher(TeacherEntity teacherEntity) {
        if (teacherEntity == null) return null;
        return TeacherResponse.builder()
                .id(teacherEntity.getId())
                .firstName(teacherEntity.getFirstName())
                .lastName(teacherEntity.getLastName())
                .patronymic(teacherEntity.getPatronymic())
                .email(teacherEntity.getUserEntity() != null ? teacherEntity.getUserEntity().getEmail() : null)
                .phoneNumber(teacherEntity.getPhoneNumber())
                .build();
    }

    @Named("mapFlashcards")
    default List<FlashcardResponse> mapFlashcards(Set<FlashcardEntity> flashcardEntities) {
        if (flashcardEntities == null) return List.of();
        return flashcardEntities.stream()
                .map(f -> FlashcardResponse.builder()
                        .id(f.getId())
                        .term(f.getTerm())
                        .definition(f.getDefinition())
                        .example(f.getExample())
                        .translation(f.getTranslation())
                        .difficulty(f.getDifficulty())
                        .lessonId(f.getLessonEntity() != null ? f.getLessonEntity().getId() : null)
                        .build())
                .collect(Collectors.toList());
    }
}

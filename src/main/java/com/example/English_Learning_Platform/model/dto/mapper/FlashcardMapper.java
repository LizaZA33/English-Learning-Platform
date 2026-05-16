package com.example.English_Learning_Platform.model.dto.mapper;

import com.example.English_Learning_Platform.model.dto.request.FlashcardRequest;
import com.example.English_Learning_Platform.model.dto.response.FlashcardResponse;
import com.example.English_Learning_Platform.model.entity.FlashcardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FlashcardMapper {
    @Mapping(target = "lessonId", source = "lessonEntity.id")
    FlashcardResponse toResponse(FlashcardEntity flashcardEntity);

    List<FlashcardResponse> toResponseList(List<FlashcardEntity> flashcardEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lessonEntity", ignore = true)
    FlashcardEntity toEntity(FlashcardRequest request);
}

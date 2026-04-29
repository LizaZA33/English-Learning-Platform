package com.example.English_Learning_Platform.model.dto.mapper;

import com.example.English_Learning_Platform.model.dto.response.LectureResponse;
import com.example.English_Learning_Platform.model.dto.response.ModuleResponse;
import com.example.English_Learning_Platform.model.entity.LectureEntity;
import com.example.English_Learning_Platform.model.entity.ModuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LectureMapper {
    @Mapping(target = "module", source = "moduleEntity", qualifiedByName = "mapModule")
    LectureResponse toResponse(LectureEntity lectureEntity);

    List<LectureResponse> toResponseList(List<LectureEntity> lectureEntities);

    @Named("mapModule")
    default ModuleResponse mapModule(ModuleEntity moduleEntity) {
        if (moduleEntity == null) return null;
        return ModuleResponse.builder()
                .id(moduleEntity.getId())
                .name(moduleEntity.getName())
                .build();
    }
}
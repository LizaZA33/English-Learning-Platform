package com.example.English_Learning_Platform.model.dto.mapper;

import com.example.English_Learning_Platform.model.dto.response.ModuleResponse;
import com.example.English_Learning_Platform.model.entity.ModuleEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModuleMapper {
    ModuleResponse toResponse(ModuleEntity moduleEntity);
    List<ModuleResponse> toResponseList(List<ModuleEntity> moduleEntities);
}

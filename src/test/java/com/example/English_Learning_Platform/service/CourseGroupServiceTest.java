package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.model.dto.mapper.CourseGroupMapper;
import com.example.English_Learning_Platform.model.dto.request.CourseGroupRequest;
import com.example.English_Learning_Platform.model.dto.response.CourseGroupResponse;
import com.example.English_Learning_Platform.model.entity.*;
import com.example.English_Learning_Platform.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseGroupServiceTest {

    @Mock
    private CourseGroupRepository groupRepository;

    @Mock
    private CourseGroupMapper groupMapper;

    @InjectMocks
    private CourseGroupService courseGroupService;

    private CourseGroupRequest request;
    private CourseGroupEntity groupEntity;
    private CourseGroupResponse groupResponse;
    private UserEntity userEntity;
    private TeacherEntity teacherEntity;
    private ModuleEntity moduleEntity;

    @BeforeEach
    void setUp() {
        request = new CourseGroupRequest();
        request.setGroupName("English Beginners");
        request.setTeacherFullName("John Doe");
        request.setModuleName("Basic English");

        userEntity = UserEntity.builder()
                .id(1L)
                .email("teacher@example.com")
                .build();

        teacherEntity = TeacherEntity.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .userEntity(userEntity)
                .build();

        moduleEntity = new ModuleEntity();
        moduleEntity.setId(1L);
        moduleEntity.setName("Basic English");

        groupEntity = new CourseGroupEntity();
        groupEntity.setId(1L);
        groupEntity.setName("English Beginners");
        groupEntity.setTeacherEntity(teacherEntity);
        groupEntity.setModuleEntity(moduleEntity);
        groupEntity.setInviteCode("abcd1234");

        groupResponse = CourseGroupResponse.builder()
                .id(1L)
                .name("English Beginners")
                .inviteCode("abcd1234")
                .studentCount(0)
                .build();
    }

    @Test
    void getGroupById_Success() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(groupEntity));
        when(groupMapper.toResponse(groupEntity)).thenReturn(groupResponse);

        CourseGroupResponse response = courseGroupService.getGroupById(1L);

        assertNotNull(response);
        assertEquals("English Beginners", response.getName());
        assertEquals("abcd1234", response.getInviteCode());
    }

    @Test
    void getGroupById_NotFound_ThrowsException() {
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> courseGroupService.getGroupById(99L));
    }
}

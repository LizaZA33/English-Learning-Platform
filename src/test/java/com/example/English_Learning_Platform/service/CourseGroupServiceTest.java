package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.exception.ValidationException;
import com.example.English_Learning_Platform.model.dto.mapper.CourseGroupMapper;
import com.example.English_Learning_Platform.model.dto.request.CourseGroupRequest;
import com.example.English_Learning_Platform.model.dto.response.CourseGroupResponse;
import com.example.English_Learning_Platform.model.entity.*;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseGroupServiceTest {

    @Mock
    private CourseGroupRepository groupRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentGroupRepository studentGroupRepository;

    @Mock
    private CourseGroupMapper groupMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private CourseGroupService courseGroupService;

    private UserEntity userEntity;
    private TeacherEntity teacherEntity;
    private ModuleEntity moduleEntity;
    private CourseGroupEntity groupEntity;
    private CourseGroupResponse groupResponse;
    private CourseGroupRequest groupRequest;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .id(1L)
                .email("teacher@test.com")
                .password("password")
                .roles(new HashSet<>(Set.of(Role.TEACHER)))
                .build();

        teacherEntity = TeacherEntity.builder()
                .id(1L)
                .userEntity(userEntity)
                .firstName("John")
                .lastName("Doe")
                .build();

        userEntity.setTeacherEntity(teacherEntity);

        moduleEntity = new ModuleEntity();
        moduleEntity.setId(1L);
        moduleEntity.setName("Test Module");

        groupEntity = new CourseGroupEntity();
        groupEntity.setId(1L);
        groupEntity.setName("Test Group");
        groupEntity.setInviteCode("abc123");
        groupEntity.setTeacherEntity(teacherEntity);
        groupEntity.setModuleEntity(moduleEntity);

        groupResponse = CourseGroupResponse.builder()
                .id(1L)
                .name("Test Group")
                .inviteCode("abc123")
                .build();

        groupRequest = new CourseGroupRequest();
        groupRequest.setGroupName("Test Group");
        groupRequest.setTeacherFullName("John Doe");
        groupRequest.setModuleName("Test Module");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("teacher@test.com");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void shouldCreateGroupWhenValidRequest() {
        mockSecurityContext();
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));
        when(teacherRepository.findByUserId(1L)).thenReturn(Optional.of(teacherEntity));
        when(moduleRepository.findByName("Test Module")).thenReturn(Optional.of(moduleEntity));
        when(groupRepository.save(any(CourseGroupEntity.class))).thenReturn(groupEntity);
        when(groupMapper.toResponse(groupEntity)).thenReturn(groupResponse);

        CourseGroupResponse result = courseGroupService.createGroup(groupRequest);

        assertNotNull(result);
        assertEquals("Test Group", result.getName());
        verify(groupRepository).save(any(CourseGroupEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnCreateGroup() {
        mockSecurityContext();
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> courseGroupService.createGroup(groupRequest));
        verify(groupRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenTeacherProfileNotFound() {
        mockSecurityContext();
        userEntity.setTeacherEntity(null);
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));
        when(teacherRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> courseGroupService.createGroup(groupRequest));
        verify(groupRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenModuleNotFoundOnCreateGroup() {
        mockSecurityContext();
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));
        when(teacherRepository.findByUserId(1L)).thenReturn(Optional.of(teacherEntity));
        when(moduleRepository.findByName("Test Module")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> courseGroupService.createGroup(groupRequest));
        verify(groupRepository, never()).save(any());
    }

    @Test
    void shouldJoinGroupWhenValidInviteCode() {
        mockSecurityContext();
        StudentEntity studentEntity = StudentEntity.builder()
                .id(1L)
                .userEntity(userEntity)
                .firstName("Student")
                .lastName("User")
                .build();

        userEntity.setRoles(new HashSet<>(Set.of(Role.USER)));
        userEntity.setTeacherEntity(null);

        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));
        when(groupRepository.findByInviteCode("abc123")).thenReturn(Optional.of(groupEntity));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(studentEntity));
        when(studentGroupRepository.existsByStudentEntityIdAndGroupId(1L, 1L)).thenReturn(false);
        when(studentGroupRepository.save(any(StudentGroupEntity.class))).thenReturn(new StudentGroupEntity());
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(groupMapper.toResponse(groupEntity)).thenReturn(groupResponse);

        CourseGroupResponse result = courseGroupService.joinGroup("abc123");

        assertNotNull(result);
        verify(studentGroupRepository).save(any(StudentGroupEntity.class));
        verify(userRepository).save(userEntity);
        assertTrue(userEntity.getRoles().contains(Role.STUDENT));
    }

    @Test
    void shouldThrowExceptionWhenGroupNotFoundByInviteCode() {
        when(groupRepository.findByInviteCode("invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> courseGroupService.joinGroup("invalid"));
    }

    @Test
    void shouldThrowExceptionWhenAlreadyMemberOfGroup() {
        mockSecurityContext();
        StudentEntity studentEntity = StudentEntity.builder()
                .id(1L)
                .userEntity(userEntity)
                .build();
        userEntity.setTeacherEntity(null);

        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));
        when(groupRepository.findByInviteCode("abc123")).thenReturn(Optional.of(groupEntity));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(studentEntity));
        when(studentGroupRepository.existsByStudentEntityIdAndGroupId(1L, 1L)).thenReturn(true);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> courseGroupService.joinGroup("abc123"));

        assertEquals("Вы уже состоите в этой группе", exception.getMessage());
    }

    @Test
    void shouldReturnGroupByIdWhenExists() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(groupEntity));
        when(groupMapper.toResponse(groupEntity)).thenReturn(groupResponse);

        CourseGroupResponse result = courseGroupService.getGroupById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(groupRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenGroupNotFoundById() {
        when(groupRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> courseGroupService.getGroupById(999L));
    }

    @Test
    void shouldReturnAllGroupsWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CourseGroupEntity> page = new PageImpl<>(List.of(groupEntity));
        when(groupRepository.findAll(pageable)).thenReturn(page);
        when(groupMapper.toResponse(groupEntity)).thenReturn(groupResponse);

        Page<CourseGroupResponse> result = courseGroupService.findAllGroups(null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnGroupsByTeacherId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CourseGroupEntity> page = new PageImpl<>(List.of(groupEntity));
        when(groupRepository.findByTeacherId(1L, pageable)).thenReturn(page);
        when(groupMapper.toResponse(groupEntity)).thenReturn(groupResponse);

        Page<CourseGroupResponse> result = courseGroupService.findAllGroups(1L, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(groupRepository).findByTeacherId(1L, pageable);
    }

    @Test
    void shouldSearchGroupsByName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CourseGroupEntity> page = new PageImpl<>(List.of(groupEntity));
        when(groupRepository.searchByName("Test", pageable)).thenReturn(page);
        when(groupMapper.toResponse(groupEntity)).thenReturn(groupResponse);

        Page<CourseGroupResponse> result = courseGroupService.findAllGroups(null, "Test", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(groupRepository).searchByName("Test", pageable);
    }

    @Test
    void shouldDeleteGroupWhenAdmin() {
        mockSecurityContext();
        userEntity.getRoles().add(Role.ADMIN);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(groupEntity));
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));

        courseGroupService.deleteGroup(1L);

        verify(groupRepository).delete(groupEntity);
    }

    @Test
    void shouldDeleteGroupWhenOwner() {
        mockSecurityContext();
        when(groupRepository.findById(1L)).thenReturn(Optional.of(groupEntity));
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));

        courseGroupService.deleteGroup(1L);

        verify(groupRepository).delete(groupEntity);
    }

    @Test
    void shouldThrowExceptionWhenDeletingGroupWithoutPermission() {
        mockSecurityContext();
        UserEntity otherUser = UserEntity.builder()
                .id(2L)
                .email("other@test.com")
                .roles(new HashSet<>(Set.of(Role.TEACHER)))
                .build();
        TeacherEntity otherTeacher = TeacherEntity.builder()
                .id(2L)
                .userEntity(otherUser)
                .build();
        otherUser.setTeacherEntity(otherTeacher);
        groupEntity.setTeacherEntity(otherTeacher);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(groupEntity));
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> courseGroupService.deleteGroup(1L));

        assertEquals("У вас нет прав на удаление этой группы", exception.getMessage());
        verify(groupRepository, never()).delete(any());
    }

    @Test
    void shouldUseArgumentCaptorWhenCreatingGroup() {
        mockSecurityContext();
        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));
        when(teacherRepository.findByUserId(1L)).thenReturn(Optional.of(teacherEntity));
        when(moduleRepository.findByName("Test Module")).thenReturn(Optional.of(moduleEntity));
        when(groupRepository.save(any(CourseGroupEntity.class))).thenReturn(groupEntity);
        when(groupMapper.toResponse(groupEntity)).thenReturn(groupResponse);

        courseGroupService.createGroup(groupRequest);

        ArgumentCaptor<CourseGroupEntity> captor = ArgumentCaptor.forClass(CourseGroupEntity.class);
        verify(groupRepository).save(captor.capture());
        CourseGroupEntity captured = captor.getValue();
        assertEquals("Test Group", captured.getName());
        assertEquals(teacherEntity, captured.getTeacherEntity());
        assertEquals(moduleEntity, captured.getModuleEntity());
    }

    @Test
    void shouldCreateStudentProfileWhenJoiningGroupWithoutProfile() {
        mockSecurityContext();
        userEntity.setTeacherEntity(null);
        userEntity.setRoles(new HashSet<>(Set.of(Role.USER)));

        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(userEntity));
        when(groupRepository.findByInviteCode("abc123")).thenReturn(Optional.of(groupEntity));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(studentRepository.save(any(StudentEntity.class))).thenAnswer(invocation -> {
            StudentEntity se = invocation.getArgument(0);
            se.setId(1L);
            return se;
        });
        when(studentGroupRepository.existsByStudentEntityIdAndGroupId(anyLong(), anyLong())).thenReturn(false);
        when(studentGroupRepository.save(any(StudentGroupEntity.class))).thenReturn(new StudentGroupEntity());
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(groupMapper.toResponse(groupEntity)).thenReturn(groupResponse);

        CourseGroupResponse result = courseGroupService.joinGroup("abc123");

        assertNotNull(result);
        verify(studentRepository).save(any(StudentEntity.class));
    }
}
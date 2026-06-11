package com.example.English_Learning_Platform.service;

import com.example.English_Learning_Platform.exception.ResourceNotFoundException;
import com.example.English_Learning_Platform.exception.ValidationException;
import com.example.English_Learning_Platform.model.dto.mapper.CourseGroupMapper;
import com.example.English_Learning_Platform.model.dto.request.CourseGroupRequest;
import com.example.English_Learning_Platform.model.dto.response.CourseGroupResponse;
import com.example.English_Learning_Platform.model.entity.*;
import com.example.English_Learning_Platform.model.enums.Role;
import com.example.English_Learning_Platform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseGroupService {

    private final CourseGroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final ModuleRepository moduleRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final CourseGroupMapper groupMapper;

    @Transactional
    public CourseGroupResponse createGroup(CourseGroupRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        TeacherEntity teacherEntity = teacherRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new ValidationException("Только учитель может создавать группы"));

        ModuleEntity moduleEntity = moduleRepository.findByName(request.getModuleName())
                .orElseThrow(() -> new ResourceNotFoundException("Модуль не найден"));

        CourseGroupEntity group = new CourseGroupEntity();
        group.setName(request.getGroupName());
        group.setTeacherEntity(teacherEntity);
        group.setModuleEntity(moduleEntity);

        CourseGroupEntity saved = groupRepository.save(group);
        log.info("Учитель {} создал группу '{}'", teacherEntity.getLastName(), saved.getName());
        return groupMapper.toResponse(saved);
    }

    @Transactional
    public CourseGroupResponse joinGroup(String inviteCode) {
        CourseGroupEntity group = groupRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new ResourceNotFoundException("Группа с таким кодом не найдена"));

        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        StudentEntity studentEntity = studentRepository.findByUserId(userEntity.getId())
                .orElseGet(() -> createStudentProfile(userEntity));

        if (studentGroupRepository.existsByStudentEntityIdAndGroupId(studentEntity.getId(), group.getId())) {
            throw new ValidationException("Вы уже состоите в этой группе");
        }

        StudentGroupEntity studentGroupEntity = StudentGroupEntity.builder()
                .studentEntity(studentEntity)
                .group(group)
                .build();
        studentGroupRepository.save(studentGroupEntity);

        userEntity.getRoles().add(Role.STUDENT);
        userRepository.save(userEntity);

        log.info("Студент {} вступил в группу '{}'", studentEntity.getLastName(), group.getName());
        return groupMapper.toResponse(group);
    }

    private StudentEntity createStudentProfile(UserEntity userEntity) {
        StudentEntity studentEntity = StudentEntity.builder()
                .userEntity(userEntity)
                .firstName("User")
                .lastName(userEntity.getEmail().split("@")[0])
                .build();
        return studentRepository.save(studentEntity);
    }

    @Transactional(readOnly = true)
    public Page<CourseGroupResponse> findAllGroups(Long teacherId, String name, Pageable pageable) {
        Page<CourseGroupEntity> page;
        if (teacherId != null) {
            page = groupRepository.findByTeacherId(teacherId, pageable);
        } else if (name != null && !name.isBlank()) {
            page = groupRepository.searchByName(name, pageable);
        } else {
            page = groupRepository.findAll(pageable);
        }
        return page.map(groupMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CourseGroupResponse getGroupById(Long id) {
        CourseGroupEntity group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Группа не найдена"));
        return groupMapper.toResponse(group);
    }

    @Transactional
    public void deleteGroup(Long id) {
        CourseGroupEntity group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Группа не найдена"));

        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity userEntity = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        boolean isAdmin = userEntity.getRoles().contains(Role.ADMIN);
        boolean isOwner = group.getTeacherEntity().getUserEntity().getId().equals(userEntity.getId());

        if (!isAdmin && !isOwner) {
            throw new ValidationException("У вас нет прав на удаление этой группы");
        }

        groupRepository.delete(group);
        log.info("Группа '{}' удалена", group.getName());
    }
    @Transactional
    public Page<CourseGroupResponse> getMyGroups(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));
        List<CourseGroupResponse> groups = new ArrayList<>();
        if (user.getStudentEntity() != null) {
            groups = user.getStudentEntity().getStudentGroupEntities().stream()
                    .map(StudentGroupEntity::getGroup)
                    .map(groupMapper::toResponse)
                    .collect(Collectors.toList());
        } else if (user.getTeacherEntity() != null) {
            groups = groupRepository.findByTeacherId(user.getTeacherEntity().getId(), Pageable.unpaged())
                    .stream()
                    .map(groupMapper::toResponse)
                    .collect(Collectors.toList());
        }
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), groups.size());
        if (start > groups.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, groups.size());
        }
        return new PageImpl<>(groups.subList(start, end), pageable, groups.size());
    }
}

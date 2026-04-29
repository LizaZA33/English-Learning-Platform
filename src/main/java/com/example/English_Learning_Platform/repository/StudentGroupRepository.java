package com.example.English_Learning_Platform.repository;

import com.example.English_Learning_Platform.model.entity.StudentGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroupEntity, Long> {
    boolean existsByStudentEntityIdAndGroupId(Long studentId, Long groupId);
}
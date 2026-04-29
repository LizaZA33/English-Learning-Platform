package com.example.English_Learning_Platform.repository;

import com.example.English_Learning_Platform.model.entity.CourseGroupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseGroupRepository extends JpaRepository<CourseGroupEntity, Long> {
    Optional<CourseGroupEntity> findByInviteCode(String inviteCode);
    @Query("SELECT g FROM CourseGroupEntity g WHERE g.teacherEntity.id = :teacherId")
    Page<CourseGroupEntity> findByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);
    @Query("SELECT g FROM CourseGroupEntity g WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<CourseGroupEntity> searchByName(@Param("name") String name, Pageable pageable);
}


package com.example.English_Learning_Platform.repository;

import com.example.English_Learning_Platform.model.entity.TeacherEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherEntity, Long> {
    @Query("SELECT t FROM TeacherEntity t WHERE t.userEntity.id = :userId")
    Optional<TeacherEntity> findByUserId(@Param("userId") Long userId);
    @Query("SELECT t FROM TeacherEntity t WHERE " +
            "LOWER(t.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.patronymic) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<TeacherEntity> searchTeachers(@Param("search") String search, Pageable pageable);
}

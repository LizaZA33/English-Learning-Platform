package com.example.English_Learning_Platform.repository;

import com.example.English_Learning_Platform.model.entity.StudentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    @Query("SELECT s FROM StudentEntity s WHERE s.userEntity.id = :userId")
    Optional<StudentEntity> findByUserId(@Param("userId") Long userId);
    @Query("SELECT s FROM StudentEntity s WHERE " +
            "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.patronymic) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<StudentEntity> searchStudents(@Param("search") String search, Pageable pageable);
}


package com.example.English_Learning_Platform.repository;

import com.example.English_Learning_Platform.model.entity.LessonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, Long> {
    @Query("SELECT l FROM LessonEntity l WHERE l.teacherEntity.id = :teacherId")
    Page<LessonEntity> findByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);
    Page<LessonEntity> findByOwnerId(Long ownerId, Pageable pageable);
    @Query("SELECT l FROM LessonEntity l WHERE LOWER(l.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<LessonEntity> searchByTitle(@Param("title") String title, Pageable pageable);
}

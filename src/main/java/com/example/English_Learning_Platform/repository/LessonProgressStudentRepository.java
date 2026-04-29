package com.example.English_Learning_Platform.repository;

import com.example.English_Learning_Platform.model.entity.LessonProgressStudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LessonProgressStudentRepository extends JpaRepository<LessonProgressStudentEntity, Long> {

    @Query("SELECT lp FROM LessonProgressStudentEntity lp WHERE lp.lessonEntity.id = :lessonId AND lp.studentEntity.id = :studentId")
    Optional<LessonProgressStudentEntity> findByLessonIdAndStudentId(Long lessonId, Long studentId);

    @Modifying
    @Transactional
    @Query("UPDATE LessonProgressStudentEntity ls SET ls.progressPercent = :progress WHERE ls.lessonEntity.id = :lessonId AND ls.studentEntity.id = :studentId")
    int updateProgressPercent(Long lessonId, Long studentId, Integer progress);
}

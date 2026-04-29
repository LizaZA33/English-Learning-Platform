package com.example.English_Learning_Platform.repository;

import com.example.English_Learning_Platform.model.entity.LectureProgressStudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LectureProgressStudentRepository extends JpaRepository<LectureProgressStudentEntity, Long> {

    @Query("SELECT lp FROM LectureProgressStudentEntity lp WHERE lp.lectureEntity.id = :lectureId AND lp.studentEntity.id = :studentId")
    Optional<LectureProgressStudentEntity> findByLectureIdAndStudentId(Long lectureId, Long studentId);

    @Modifying
    @Transactional
    @Query("UPDATE LectureProgressStudentEntity ls SET ls.progressPercent = :progress WHERE ls.lectureEntity.id = :lectureId AND ls.studentEntity.id = :studentId")
    int updateProgressPercent(Long lectureId, Long studentId, Integer progress);
}

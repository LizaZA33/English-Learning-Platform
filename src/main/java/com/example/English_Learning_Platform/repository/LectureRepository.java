package com.example.English_Learning_Platform.repository;

import com.example.English_Learning_Platform.model.entity.LectureEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepository extends JpaRepository<LectureEntity, Long> {
    Page<LectureEntity> findByModuleEntityId(Long moduleId, Pageable pageable);

    @Query("SELECT l FROM LectureEntity l WHERE LOWER(l.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<LectureEntity> searchByTitle(@Param("title") String title, Pageable pageable);
}

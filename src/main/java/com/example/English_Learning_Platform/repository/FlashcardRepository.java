package com.example.English_Learning_Platform.repository;

import com.example.English_Learning_Platform.model.entity.FlashcardEntity;
import com.example.English_Learning_Platform.model.entity.LessonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashcardRepository extends JpaRepository<FlashcardEntity, Long> {
    Page<FlashcardEntity> findByLessonEntity(LessonEntity lessonEntity, Pageable pageable);
    Page<FlashcardEntity> findByTermContainingIgnoreCase(String term, Pageable pageable);
}
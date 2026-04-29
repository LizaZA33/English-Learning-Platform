package com.example.English_Learning_Platform.repository;
import com.example.English_Learning_Platform.model.entity.ModuleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {
    Optional<ModuleEntity> findByName(String name);
    Page<ModuleEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}

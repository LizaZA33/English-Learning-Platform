package com.example.English_Learning_Platform.repository;

import com.example.English_Learning_Platform.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM UserEntity u WHERE " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "EXISTS (SELECT t FROM TeacherEntity t WHERE t.userEntity = u AND " +
            "(LOWER(t.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(t.patronymic) LIKE LOWER(CONCAT('%', :search, '%')))) OR " +
            "EXISTS (SELECT s FROM StudentEntity s WHERE s.userEntity = u AND " +
            "(LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.patronymic) LIKE LOWER(CONCAT('%', :search, '%'))))")
    Page<UserEntity> searchUsers(@Param("search") String search, Pageable pageable);
}

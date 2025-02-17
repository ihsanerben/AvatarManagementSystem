package com.ihsan.repository;

import com.ihsan.entity.FileEntity;
import com.ihsan.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByFileName(String fileName);
    boolean existsByFileName(String fileName);

    Optional<FileEntity> findByUser(UserEntity user);

    @Transactional
    Optional<FileEntity> findByUserId(Long userId);
}

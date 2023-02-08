package com.backend.gitssum.repository;

import com.backend.gitssum.entity.Recommendation;
import com.backend.gitssum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Optional<Recommendation> findByUserId(Long userId);

    boolean existsByUserId(Long id);

    void deleteByUserId(Long userId);
}

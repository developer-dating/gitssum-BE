package com.backend.gitssum.repository;

import com.backend.gitssum.entity.LikeUser;
import com.backend.gitssum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeUser, Long> {

    boolean existsByLikedUserIdAndLikerUserId(Long likedUserId, Long likerUserId);

    void deleteByLikedUserIdAndLikerUserId(Long id, Long userId);

}

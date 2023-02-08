package com.backend.gitssum.repository;

import com.backend.gitssum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

        Optional<User> findByEmail(String email);
        Optional<User> findByKakaoId(Long kakaoId);

        List<User> findAllByOrderByModifiedAtDesc();

        //나 자신 제외한 user
//        @Query("select u from User u where u.id = Long userId")
//        List <User> userswithoutme(Long userId);

      //나이대별
        @Query("select u from User u where u.age between 20L and 24L")
        List <User> findallusers20to24();

        @Query("select u from User u where u.age between 25L and 29L")
        List <User> findallusers25to29();

        @Query("select u from User u where u.age between 30L and 34L")
        List <User> findallusers30to34();

        @Query("select u from User u where u.age between 35L and 39L")
        List <User> findallusers35to39();

}

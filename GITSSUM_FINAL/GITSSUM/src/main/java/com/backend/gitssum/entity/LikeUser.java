package com.backend.gitssum.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class LikeUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long likerUserId;

    @Column
    private Long likedUserId;




    public LikeUser(Long likerUserId,Long likedUserId) {
        this.likerUserId = likerUserId;
        this.likedUserId = likedUserId;

    }

}

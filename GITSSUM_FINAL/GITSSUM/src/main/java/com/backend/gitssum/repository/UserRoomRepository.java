package com.backend.gitssum.repository;

import com.backend.gitssum.entity.Room;
import com.backend.gitssum.entity.UserRoom;
import com.backend.gitssum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRoomRepository extends JpaRepository<UserRoom,Long> {
    UserRoom findByRoomAndUser(Room room, User user);
    UserRoom findByRoomAndUserAndToUser(Room room, User user,User toUser);
    List<UserRoom> findByUser(User user);
    List<UserRoom> findByRoom(Room room);
}

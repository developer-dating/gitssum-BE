package com.backend.gitssum.repository;

import com.backend.gitssum.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Long> {


    Optional<Room> findByRoomName(String roomName);
}
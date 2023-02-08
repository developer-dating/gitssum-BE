package com.backend.gitssum.repository;

import com.backend.gitssum.entity.Message;
import com.backend.gitssum.entity.Room;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    PageImpl<Message> findByRoom(Room room, Pageable pageable);
    void deleteAllByRoom(Room room);
}

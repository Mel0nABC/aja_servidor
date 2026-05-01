package dev.aja.aja.directmessage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.aja.aja.directmessage.entity.DirectMessageEntity;

public interface DirectMessageRepository extends JpaRepository<DirectMessageEntity, Long> {

}

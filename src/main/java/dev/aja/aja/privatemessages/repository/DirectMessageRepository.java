package dev.aja.aja.privatemessages.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.aja.aja.privatemessages.entity.DirectMessageEntity;

public interface DirectMessageRepository extends JpaRepository<DirectMessageEntity, Long> {

}

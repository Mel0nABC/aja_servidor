package dev.aja.aja.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.aja.aja.forum.entity.ForumEntity;

public interface ForumRepository extends JpaRepository<ForumEntity, Long> {

}

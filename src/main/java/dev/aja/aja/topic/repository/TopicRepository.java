package dev.aja.aja.topic.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.aja.aja.topic.entity.TopicEntity;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {

}

package dev.aja.aja.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.aja.aja.post.entity.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

}

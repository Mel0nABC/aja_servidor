package dev.aja.aja.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.aja.aja.auth.entity.UserEntity;

// https://docs.spring.io/spring-data/jpa/reference/data-commons/repositories/definition.html
@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

}

package com.arkaback.repository;

import com.arkaback.entity.person.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PersonJpaRepository extends JpaRepository<PersonEntity, Long> {

    Optional<PersonEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}

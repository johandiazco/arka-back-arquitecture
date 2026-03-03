package com.arkaback.adapter;

import com.arkaback.entity.person.Person;
import com.arkaback.entity.person.PersonEntity;
import com.arkaback.ports.output.PersonRepository;
import com.arkaback.repository.PersonJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PersonRepositoryAdapter implements PersonRepository {

    private final PersonJpaRepository jpaRepository;

    @Override
    public Optional<Person> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Person save(Person person) {
        PersonEntity entity = PersonEntity.builder()
                .id(person.getId())
                .name(person.getName())
                .email(person.getEmail())
                .passwordHash(person.getPasswordHash())
                .phone(person.getPhone())
                .address(person.getAddress())
                .isActive(person.isActive())
                .build();
        return toDomain(jpaRepository.save(entity));
    }

    private Person toDomain(PersonEntity entity) {
        return Person.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .isActive(entity.getIsActive())
                .build();
    }
}

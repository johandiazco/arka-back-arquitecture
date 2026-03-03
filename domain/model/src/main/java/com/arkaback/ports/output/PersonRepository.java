package com.arkaback.ports.output;

import com.arkaback.entity.person.Person;
import java.util.Optional;

public interface PersonRepository {
    Optional<Person> findByEmail(String email);
    boolean existsByEmail(String email);
    Person save(Person person);
}

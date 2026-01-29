package com.arkaback.entity.person;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private Long id;
    private String name;
    private String email;
    private String passwordHash;
    private String phone;
    private String address;
    private boolean isActive;

}

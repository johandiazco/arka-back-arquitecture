package com.arkaback.entity;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    private long id;
    private String name;
    private String identification;
    private String email;
    private String phone;
    private String address;
    private String country;
    private boolean isActive;

}

package com.arkaback.entity.supplier;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    private Long id;
    private String name;
    private String identification;
    private String email;
    private String phone;
    private String address;
    private String country;
    private boolean isActive;

}

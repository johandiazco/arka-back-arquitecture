package com.arkaback.entity.warehouse;

import lombok.*;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse {

    private Long id;
    private String name;
    private String country;
    private String city;
    private String address;
    private String phone;
    private boolean isActive;

}

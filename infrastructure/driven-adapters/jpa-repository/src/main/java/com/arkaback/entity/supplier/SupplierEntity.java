package com.arkaback.entity.supplier;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "suppliers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String identification;
    private String email;
    private String phone;
    private String address;
    private String country;

    @Column(name = "is_active")
    private Boolean isActive;

}

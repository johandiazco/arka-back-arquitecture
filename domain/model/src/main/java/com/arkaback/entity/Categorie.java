package com.arkaback.entity;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categorie {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
}

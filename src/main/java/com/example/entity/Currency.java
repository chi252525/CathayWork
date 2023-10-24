package com.example.entity;


import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private String name;

    private BigDecimal price;

    public Currency() {
    }

    public Currency(String code, String name, BigDecimal price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }
}


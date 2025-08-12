package com.fundicion.lara.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class ProductEntity extends AuditCommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "unidad", length = 10)
    private String unidad;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "purchase_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "avatar")
    private String avatar;

}

package com.example.ecommerce.backend.cart.entity;

import com.example.ecommerce.backend.common.entity.BaseEntity;
import com.example.ecommerce.backend.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_cart_product",
                columnNames = {"cart_id", "product_id"}
        ))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double unitPrice;
}

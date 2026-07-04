package com.example.ecommerce.backend.cart.entity;

import com.example.ecommerce.backend.common.entity.Auditable;
import com.example.ecommerce.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(
        name = "Cart.withItems",
        attributeNodes = @NamedAttributeNode("items")
)
public class Cart extends BaseEntity implements Auditable {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "modified_by")
    private Long modifiedBy;
}

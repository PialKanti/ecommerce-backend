package com.example.ecommerce.backend.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Base mapped superclass for persistent entities.
 *
 * <p>Provides the generated primary key and lifecycle hooks that populate audit
 * timestamps for entities implementing {@link Auditable}.</p>
 *
 * @author Pial Kanti Samadder
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * Initializes audit timestamps before the entity is first persisted.
     */
    @PrePersist
    protected void onCreate() {
        if (this instanceof Auditable auditable) {
            LocalDateTime now = LocalDateTime.now();
            auditable.setCreatedAt(now);
            auditable.setModifiedAt(now);
        }
    }

    /**
     * Refreshes the modification timestamp before an existing entity is updated.
     */
    @PreUpdate
    protected void onUpdate() {
        if (this instanceof Auditable auditable) {
            auditable.setModifiedAt(LocalDateTime.now());
        }
    }
}

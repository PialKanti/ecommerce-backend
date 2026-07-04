package com.example.ecommerce.backend.common.entity;

import java.time.LocalDateTime;

/**
 * Contract for entities that store creation and modification audit metadata.
 *
 * <p>{@link BaseEntity} uses this interface to populate timestamp fields during
 * persistence lifecycle events.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface Auditable {
    /**
     * Returns the timestamp when the entity was created.
     *
     * @return creation timestamp
     */
    LocalDateTime getCreatedAt();

    /**
     * Sets the timestamp when the entity was created.
     *
     * @param createdAt creation timestamp
     */
    void setCreatedAt(LocalDateTime createdAt);

    /**
     * Returns the timestamp when the entity was last modified.
     *
     * @return last modification timestamp
     */
    LocalDateTime getModifiedAt();

    /**
     * Sets the timestamp when the entity was last modified.
     *
     * @param modifiedAt last modification timestamp
     */
    void setModifiedAt(LocalDateTime modifiedAt);

    /**
     * Returns the identifier of the user that created the entity.
     *
     * @return creator user identifier
     */
    Long getCreatedBy();

    /**
     * Sets the identifier of the user that created the entity.
     *
     * @param createdBy creator user identifier
     */
    void setCreatedBy(Long createdBy);

    /**
     * Returns the identifier of the user that last modified the entity.
     *
     * @return last modifier user identifier
     */
    Long getModifiedBy();

    /**
     * Sets the identifier of the user that last modified the entity.
     *
     * @param modifiedBy last modifier user identifier
     */
    void setModifiedBy(Long modifiedBy);
}

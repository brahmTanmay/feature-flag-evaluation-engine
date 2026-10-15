package com.featureflags.model;

import java.util.Objects;

public final class EntityRef {
    private final String entityId;
    private final com.featureflags.model.EntityType entityType;

    public EntityRef(String entityId, com.featureflags.model.EntityType entityType) {
        if (entityId == null || entityId.trim().isEmpty()) {
            throw new IllegalArgumentException("entityId is required");
        }
        if (entityType == null) {
            throw new IllegalArgumentException("entityType is required");
        }
        this.entityId = entityId.trim();
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public com.featureflags.model.EntityType getEntityType() {
        return entityType;
    }

    public String key() {
        return entityType.name() + ":" + entityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityRef)) {
            return false;
        }
        EntityRef entityRef = (EntityRef) o;
        return entityId.equals(entityRef.entityId) && entityType == entityRef.entityType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, entityType);
    }

    @Override
    public String toString() {
        return "EntityRef{" +
                "entityId='" + entityId + '\'' +
                ", entityType=" + entityType +
                '}';
    }
}

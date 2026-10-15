package com.featureflags.flag;

import com.featureflags.hash.StableHasher;
import com.featureflags.model.EntityRef;
import com.featureflags.model.EntityType;
import com.featureflags.model.FlagType;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public final class AllowlistFlagDefinition implements FlagDefinition {
    private final String name;
    private final Set<String> allowedEntityKeys;
    public final String falseResponse = "false";
    public final String trueResponse = "true";

    public AllowlistFlagDefinition(String name, Collection<String> allowedEntityKeys) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }
        if (allowedEntityKeys == null) {
            throw new IllegalArgumentException("allowedEntityKeys is required");
        }
        this.name = name.trim();
        this.allowedEntityKeys = Collections.unmodifiableSet(canonicalize(allowedEntityKeys));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FlagType getType() {
        return FlagType.ALLOWLIST;
    }

    public Set<String> getAllowedEntityKeys() {
        return allowedEntityKeys;
    }

    @Override
    public String evaluate(EntityRef entity, StableHasher hasher) {
        if(allowedEntityKeys.contains(entity.key())) {
            return trueResponse;
        }
        return falseResponse;
    }

    private Set<String> canonicalize(Collection<String> rawKeys) {
        Set<String> normalized = new LinkedHashSet<String>();
        for (String rawKey : rawKeys) {
            normalized.add(canonicalEntityKey(rawKey));
        }
        return normalized;
    }

    private String canonicalEntityKey(String rawKey) {
        if (rawKey == null || rawKey.trim().isEmpty()) {
            throw new IllegalArgumentException("allowlist entry must be non-empty");
        }
        int delimiter = rawKey.indexOf(':');
        if (delimiter <= 0 || delimiter == rawKey.length() - 1) {
            throw new IllegalArgumentException("allowlist entry must be in format ENTITY_TYPE:entityId");
        }

        String rawType = rawKey.substring(0, delimiter).trim().toUpperCase(Locale.ROOT);
        String entityId = rawKey.substring(delimiter + 1).trim();
        if (entityId.isEmpty()) {
            throw new IllegalArgumentException("allowlist entityId must be non-empty");
        }

        EntityType entityType;
        try {
            entityType = EntityType.valueOf(rawType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("unsupported entity type in allowlist: " + rawType, e);
        }

        return entityType.name() + ":" + entityId;
    }
}

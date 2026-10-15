package com.featureflags.engine;

import com.featureflags.flag.FlagDefinition;
import com.featureflags.hash.StableHasher;
import com.featureflags.model.EntityRef;
import com.featureflags.model.EntityType;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DefaultFlagEvaluationEngine implements FlagEvaluationEngine {
    private final Map<String, FlagDefinition> flagsByName;
    private final StableHasher hasher;
    public static String falseResponse = "false";

    public DefaultFlagEvaluationEngine(Map<String, FlagDefinition> flagsByName, StableHasher hasher) {
        if (flagsByName == null) {
            throw new IllegalArgumentException("flagsByName is required");
        }
        if (hasher == null) {
            throw new IllegalArgumentException("hasher is required");
        }

        Map<String, FlagDefinition> copy = new LinkedHashMap<String, FlagDefinition>();
        for (Map.Entry<String, FlagDefinition> entry : flagsByName.entrySet()) {
            String normalizedName = normalizeFlagName(entry.getKey());
            FlagDefinition definition = entry.getValue();
            if (definition == null) {
                throw new IllegalArgumentException("flag definition is required for " + normalizedName);
            }
            if (copy.containsKey(normalizedName)) {
                throw new IllegalArgumentException("duplicate flag name: " + normalizedName);
            }
            copy.put(normalizedName, definition);
        }

        this.flagsByName = Collections.unmodifiableMap(copy);
        this.hasher = hasher;
    }

    @Override
    public String evaluate(String flagName, String entityId, EntityType entityType) {
        if (flagName == null || flagName.trim().isEmpty()) {
            return falseResponse;
        }
        FlagDefinition definition = flagsByName.get(normalizeFlagName(flagName));
        if (definition == null) {
            return falseResponse;
        }
        try {
            return definition.evaluate(new EntityRef(entityId, entityType), hasher);
        } catch (IllegalArgumentException e) {
            return falseResponse;
        }
    }

    public Map<String, FlagDefinition> getFlagsByName() {
        return flagsByName;
    }

    private String normalizeFlagName(String flagName) {
        if (flagName == null || flagName.trim().isEmpty()) {
            throw new IllegalArgumentException("flagName is required");
        }
        return flagName.trim();
    }
}

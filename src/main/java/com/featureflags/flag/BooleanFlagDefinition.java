package com.featureflags.flag;

import com.featureflags.hash.StableHasher;
import com.featureflags.model.EntityRef;
import com.featureflags.model.FlagType;

public final class BooleanFlagDefinition implements FlagDefinition {
    private final String name;
    private final boolean enabled;
    public final String falseResponse = "false";
    public final String trueResponse = "true";

    public BooleanFlagDefinition(String name, boolean enabled) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }
        this.name = name.trim();
        this.enabled = enabled;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FlagType getType() {
        return FlagType.BOOLEAN;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String evaluate(EntityRef entity, StableHasher hasher) {
        if(enabled) {
            return trueResponse;
        }
        return falseResponse;
    }
}

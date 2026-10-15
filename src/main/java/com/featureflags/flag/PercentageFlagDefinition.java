package com.featureflags.flag;

import com.featureflags.hash.StableHasher;
import com.featureflags.model.EntityRef;
import com.featureflags.model.FlagType;

public final class PercentageFlagDefinition implements FlagDefinition {
    private static final int FULL_RANGE = 100;

    private final String name;
    private final int percentage;
    private final String salt;
    private final String falseResponse = "false";
    private final String trueResponse = "true";

    public PercentageFlagDefinition(String name, int percentage, String salt) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name is required");
        }
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("percentage must be in range 0-100");
        }
        this.name = name.trim();
        this.percentage = percentage;
        this.salt = (salt == null || salt.trim().isEmpty()) ? this.name : salt.trim();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public FlagType getType() {
        return FlagType.PERCENTAGE;
    }

    public int getPercentage() {
        return percentage;
    }

    public String getSalt() {
        return salt;
    }

    @Override
    public String evaluate(EntityRef entity, StableHasher hasher) {
        if (percentage <= 0) {
            return falseResponse;
        }
        if (percentage >= 100) {
            return trueResponse;
        }
        int bucket = hasher.bucket(name + "|" + salt + "|" + entity.key(), FULL_RANGE);
        if(bucket < percentage) {
            return trueResponse;
        }
        return falseResponse;
    }
}

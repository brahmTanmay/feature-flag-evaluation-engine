package com.featureflags.flag;

import com.featureflags.hash.StableHasher;
import com.featureflags.model.EntityRef;
import com.featureflags.model.FlagType;

public interface FlagDefinition {
    String getName();

    FlagType getType();

    String evaluate(EntityRef entity, StableHasher hasher);
}

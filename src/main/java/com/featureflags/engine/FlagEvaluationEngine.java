package com.featureflags.engine;

import com.featureflags.model.EntityType;

public interface FlagEvaluationEngine {
    String evaluate(String flagName, String entityId, EntityType entityType);
}

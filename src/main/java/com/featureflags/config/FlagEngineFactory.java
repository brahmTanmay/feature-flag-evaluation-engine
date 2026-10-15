package com.featureflags.config;

import com.featureflags.engine.DefaultFlagEvaluationEngine;
import com.featureflags.engine.FlagEvaluationEngine;
import com.featureflags.flag.FlagDefinition;
import com.featureflags.hash.Sha256StableHasher;
import com.featureflags.hash.StableHasher;

import java.util.Map;

public final class FlagEngineFactory {
    private final FlagConfigParser parser;
    private final StableHasher hasher;

    public FlagEngineFactory() {
        this(new FlagConfigParser(), new Sha256StableHasher());
    }

    public FlagEngineFactory(FlagConfigParser parser, StableHasher hasher) {
        if (parser == null) {
            throw new IllegalArgumentException("parser is required");
        }
        if (hasher == null) {
            throw new IllegalArgumentException("hasher is required");
        }
        this.parser = parser;
        this.hasher = hasher;
    }

    public FlagEvaluationEngine create(Map<String, Map<String, Object>> rawConfig) {
        Map<String, FlagDefinition> parsedFlags = parser.parse(rawConfig);
        return new DefaultFlagEvaluationEngine(parsedFlags, hasher);
    }
}

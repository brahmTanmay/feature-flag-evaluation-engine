package com.featureflags;

import com.featureflags.config.FlagEngineFactory;
import com.featureflags.engine.FlagEvaluationEngine;
import com.featureflags.model.EntityType;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Main {
    public static void main(String[] args) {
        FlagEvaluationEngine engine = new FlagEngineFactory().create(sampleConfig());

        printResult(engine, "new_checkout", "1", EntityType.USER);
        printResult(engine, "new_checkout", "42", EntityType.ACCOUNT);

        printResult(engine, "beta_search", "1", EntityType.USER);
        printResult(engine, "beta_search", "2", EntityType.USER);
        printResult(engine, "beta_search", "1", EntityType.USER);

        printResult(engine, "vip_support", "1", EntityType.USER);
        printResult(engine, "vip_support", "2", EntityType.USER);
        printResult(engine, "vip_support", "acme", EntityType.ACCOUNT);
        printResult(engine, "vip_support", "1", EntityType.ACCOUNT);

        printResult(engine, "always_off", "99", EntityType.USER);
        printResult(engine, "always_on", "99", EntityType.USER);

        printResult(engine, "unknown_flag", "1", EntityType.USER);
        printResult(engine, "new_checkout", "   ", EntityType.USER);
    }

    private static Map<String, Map<String, Object>> sampleConfig() {
        Map<String, Map<String, Object>> config = new LinkedHashMap<String, Map<String, Object>>();

        Map<String, Object> newCheckout = new LinkedHashMap<String, Object>();
        newCheckout.put("type", "BOOLEAN");
        newCheckout.put("enabled", true);
        config.put("new_checkout", newCheckout);

        Map<String, Object> betaSearch = new LinkedHashMap<String, Object>();
        betaSearch.put("type", "PERCENTAGE");
        betaSearch.put("percentage", 30);
        betaSearch.put("salt", "v1");
        config.put("beta_search", betaSearch);

        Map<String, Object> vipSupport = new LinkedHashMap<String, Object>();
        vipSupport.put("type", "ALLOWLIST");
        List<String> allowlist = Arrays.asList("USER:1", "USER:7", "ACCOUNT:acme");
        vipSupport.put("entities", allowlist);
        config.put("vip_support", vipSupport);

        Map<String, Object> alwaysOff = new LinkedHashMap<String, Object>();
        alwaysOff.put("type", "PERCENTAGE");
        alwaysOff.put("percentage", 0);
        config.put("always_off", alwaysOff);

        Map<String, Object> alwaysOn = new LinkedHashMap<String, Object>();
        alwaysOn.put("type", "PERCENTAGE");
        alwaysOn.put("percentage", 100);
        config.put("always_on", alwaysOn);

        return config;
    }

    private static void printResult(
            FlagEvaluationEngine engine,
            String flagName,
            String entityId,
            EntityType entityType
    ) {
        String enabled = engine.evaluate(flagName, entityId, entityType);
        System.out.println(
                "flag=" + flagName
                        + ", entityType=" + entityType
                        + ", entityId=" + quote(entityId)
                        + " => " + enabled
        );
    }

    private static String quote(String value) {
        return "'" + value + "'";
    }
}

package com.featureflags.config;

import com.featureflags.flag.AllowlistFlagDefinition;
import com.featureflags.flag.BooleanFlagDefinition;
import com.featureflags.flag.FlagDefinition;
import com.featureflags.flag.PercentageFlagDefinition;
import com.featureflags.model.FlagType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class FlagConfigParser {
    public Map<String, FlagDefinition> parse(Map<String, Map<String, Object>> rawConfig) {
        if (rawConfig == null) {
            throw new IllegalArgumentException("rawConfig is required");
        }

        Map<String, FlagDefinition> parsed = new LinkedHashMap<String, FlagDefinition>();
        for (Map.Entry<String, Map<String, Object>> entry : rawConfig.entrySet()) {
            String flagName = normalizeFlagName(entry.getKey());
            Map<String, Object> definitionMap = entry.getValue();
            if (definitionMap == null) {
                throw new IllegalArgumentException("flag definition map is required for " + flagName);
            }
            parsed.put(flagName, parseDefinition(flagName, definitionMap));
        }
        return Collections.unmodifiableMap(parsed);
    }

    private FlagDefinition parseDefinition(String flagName, Map<String, Object> definitionMap) {
        FlagType type = parseFlagType(definitionMap.get("type"));
        switch (type) {
            case BOOLEAN:
                return new BooleanFlagDefinition(flagName, parseBoolean(definitionMap, "enabled", "check"));
            case PERCENTAGE:
                int percentage = parseInt(definitionMap, "percentage", "percent");
                String salt = parseOptionalString(definitionMap.get("salt"));
                return new PercentageFlagDefinition(flagName, percentage, salt);
            case ALLOWLIST:
                return new AllowlistFlagDefinition(flagName, parseStringList(definitionMap, "entities", "allowlist"));
            default:
                throw new IllegalArgumentException("unsupported flag type: " + type);
        }
    }

    private FlagType parseFlagType(Object rawType) {
        if (rawType == null) {
            throw new IllegalArgumentException("flag type is required");
        }
        String typeName = rawType.toString().trim().toUpperCase(Locale.ROOT);
        if (typeName.equals("PERCENT")) {
            typeName = "PERCENTAGE";
        }
        return FlagType.valueOf(typeName);
    }

    private boolean parseBoolean(Map<String, Object> map, String primaryKey, String fallbackKey) {
        Object value = map.containsKey(primaryKey) ? map.get(primaryKey) : map.get(fallbackKey);
        if (value == null) {
            throw new IllegalArgumentException("missing boolean value for keys: " + primaryKey + "/" + fallbackKey);
        }
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        if (value instanceof String) {
            String raw = ((String) value).trim().toLowerCase(Locale.ROOT);
            if ("true".equals(raw)) {
                return true;
            }
            if ("false".equals(raw)) {
                return false;
            }
        }
        throw new IllegalArgumentException("invalid boolean value: " + value);
    }

    private int parseInt(Map<String, Object> map, String primaryKey, String fallbackKey) {
        Object value = map.containsKey(primaryKey) ? map.get(primaryKey) : map.get(fallbackKey);
        if (value == null) {
            throw new IllegalArgumentException("missing integer value for keys: " + primaryKey + "/" + fallbackKey);
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            return Integer.parseInt(((String) value).trim());
        }
        throw new IllegalArgumentException("invalid integer value: " + value);
    }

    private String parseOptionalString(Object value) {
        if (value == null) {
            return null;
        }
        String raw = value.toString().trim();
        return raw.isEmpty() ? null : raw;
    }

    private List<String> parseStringList(Map<String, Object> map, String primaryKey, String fallbackKey) {
        Object value = map.containsKey(primaryKey) ? map.get(primaryKey) : map.get(fallbackKey);
        if (value == null) {
            throw new IllegalArgumentException("missing list value for keys: " + primaryKey + "/" + fallbackKey);
        }
        if (!(value instanceof Collection)) {
            throw new IllegalArgumentException("allowlist must be a collection");
        }
        List<String> result = new ArrayList<String>();
        for (Object item : (Collection<?>) value) {
            if (item == null) {
                throw new IllegalArgumentException("allowlist items must be non-null");
            }
            result.add(item.toString());
        }
        return result;
    }

    private String normalizeFlagName(String flagName) {
        if (flagName == null || flagName.trim().isEmpty()) {
            throw new IllegalArgumentException("flag name is required");
        }
        return flagName.trim();
    }
}

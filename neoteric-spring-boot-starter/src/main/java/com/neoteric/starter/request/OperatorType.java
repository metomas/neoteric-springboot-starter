package com.neoteric.starter.request;

import java.util.HashMap;
import java.util.Map;

public enum OperatorType {
    EQUAL("$eq", ValueType.SCALAR),
    LESS_THAN("$lt", ValueType.SCALAR),
    STARTS_WITH("$startsWith", ValueType.SCALAR),
    IN("$in", ValueType.ARRAY);

    private String value;
    private ValueType valueType;

    private static class Holder {
        static Map<String, OperatorType> MAP = new HashMap<>();
    }

    OperatorType(String value, ValueType valueType) {
        this.value = value;
        this.valueType = valueType;
        Holder.MAP.put(value, this);
    }

    public String getName() {
        return value;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public static OperatorType fromString(String name) {
        OperatorType operatorType = Holder.MAP.get(name);
        if(operatorType == null) {
            throw new IllegalStateException(String.format("Unsupported type %s.", name));
        }
        return operatorType;
    }

    public static boolean contains(String name) {
        return Holder.MAP.containsKey(name);
    }
}

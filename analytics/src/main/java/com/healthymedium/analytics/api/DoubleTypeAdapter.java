package com.healthymedium.analytics.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.MathContext;

public class DoubleTypeAdapter implements JsonSerializer<Double> {

    @Override
    public JsonElement serialize(Double src, Type srcType, JsonSerializationContext context) {
        MathContext mc = new MathContext(15);
        BigDecimal value = BigDecimal.valueOf(src);
        return new JsonPrimitive(new BigDecimal(value.doubleValue(), mc));
    }

}

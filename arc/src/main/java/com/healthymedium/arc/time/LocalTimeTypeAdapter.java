package com.healthymedium.arc.time;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.LocalTime;
import java.lang.reflect.Type;

public class LocalTimeTypeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    @Override
    public JsonElement serialize(LocalTime src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public LocalTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
       // long millis = json.getAsJsonObject().get("iLocalMillis").getAsLong();
        return new LocalTime(json.getAsString());
    }

}

package com.healthymedium.arc.time;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.healthymedium.analytics.Log;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.lang.reflect.Type;
import java.util.Locale;

public class LocalTimeTypeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    DateTimeFormatter formatter;

    public LocalTimeTypeAdapter(){
        formatter = new DateTimeFormatterBuilder()
                .appendPattern("h:mm a")
                .toFormatter()
                .withLocale(Locale.US);
    }

    @Override
    public JsonElement serialize(LocalTime src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString(formatter));
    }

    @Override
    public LocalTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        String string = json.getAsString();
        LocalTime time = null;
        try {
            time = LocalTime.parse(string,formatter);
        } catch (IllegalArgumentException e) {
            Log.system.w("LocalTimeTypeAdapter","failed to deserialize '"+string+"', attempting fallback");
            string = string
                    .replace("a.m.","AM")
                    .replace("p.m.","PM")
                    .replace("午前","AM")
                    .replace("午後","PM");
            time = LocalTime.parse(string,formatter);
        }
        return time;
    }

}

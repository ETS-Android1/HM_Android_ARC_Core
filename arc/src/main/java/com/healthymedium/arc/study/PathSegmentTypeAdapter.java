package com.healthymedium.arc.study;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class PathSegmentTypeAdapter implements JsonSerializer<PathSegment>{

    @Override
    public JsonElement serialize(PathSegment pathSegment, Type srcType, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("currentIndex", new JsonPrimitive(pathSegment.currentIndex));
        if (pathSegment.dataObject != null) {
            result.add("dataObject", context.serialize(pathSegment.dataObject));
        }
        return result;
    }

}

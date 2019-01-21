package com.healthymedium.arc.api.models;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Response {

    public int code;
    public boolean successful;
    public JsonObject optional = new JsonObject();
    public JsonObject errors = new JsonObject();

}

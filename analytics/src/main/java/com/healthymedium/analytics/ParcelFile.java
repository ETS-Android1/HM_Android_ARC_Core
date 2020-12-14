package com.healthymedium.analytics;

public class ParcelFile {

    public static final String TEXT = "text/plain";
    public static final String JSON = "application/json";
    public static final String XML = "application/xml";

    String name;
    String mime;

    public ParcelFile(String filename, String mime) {
        name = filename;
        this.mime = mime;
    }

}

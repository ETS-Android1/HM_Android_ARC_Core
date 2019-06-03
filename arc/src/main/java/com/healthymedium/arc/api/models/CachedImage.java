package com.healthymedium.arc.api.models;

import com.healthymedium.arc.utilities.CacheManager;

import java.io.File;
import java.lang.reflect.Field;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CachedImage  extends CachedObject{

    CachedImage(){
        super();
        mediaType = "image/*";
    }
}

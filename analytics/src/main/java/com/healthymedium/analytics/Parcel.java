package com.healthymedium.analytics;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Parcel {

    List<ParcelFile> files;
    String report;

    public Parcel(String report, List<ParcelFile> files) {
        this.report = report;
        this.files = files;
    }

    public RequestBody getRequestBody(){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        builder.addFormDataPart("report",report);

        for(ParcelFile parcelFile : files) {
            File file = new File(parcelFile.name);

            if(!file.exists()){
                return null;
            }

            RequestBody uploadFile = RequestBody.create(MediaType.parse(parcelFile.mime),file);
            builder.addFormDataPart("file-"+file.getName(),file.getName(),uploadFile);
        }

        return builder.build();
    }

}

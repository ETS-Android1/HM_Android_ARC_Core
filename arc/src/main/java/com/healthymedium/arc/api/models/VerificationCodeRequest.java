package com.healthymedium.arc.api.models;

import com.google.gson.annotations.SerializedName;

public class VerificationCodeRequest {
    @SerializedName("participant_id")
    private String arcId;

    public String getArcId() {
        return arcId;
    }

    public void setArcId(String arcId) {
        this.arcId = arcId;
    }
}

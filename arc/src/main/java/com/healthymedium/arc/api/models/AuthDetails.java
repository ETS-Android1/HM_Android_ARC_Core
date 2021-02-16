package com.healthymedium.arc.api.models;

import com.google.gson.annotations.SerializedName;

public class AuthDetails {

    public static final transient String TYPE_RATER = "rater";                  // The auth code is a Rater id (like HASD)
    public static final transient String TYPE_CONFIRM_CODE = "confirm_code";    // The auth code is a 2fa code. This action will also send the participant their 2fa code (like EXR)
    public static final transient String TYPE_MANUAL = "manual";                // The auth code is a password that was set by the researchers and given to the participant (like Hershey)

    @SerializedName("study_name")
    private String studyName;

    @SerializedName("auth_type")
    private String authType;

    @SerializedName("auth_code_length")
    private Integer authCodeLength;

    public String getStudyName() {
        return studyName;
    }

    public String getType() {
        return authType;
    }

    public boolean isTypeRater() {
        return authType.equals(TYPE_RATER);
    }

    public boolean isTypeConfirmCode() {
        return authType.equals(TYPE_CONFIRM_CODE);
    }

    public boolean isTypeManual() {
        return authType.equals(TYPE_MANUAL);
    }

    public Integer getCodeLength() {
        return authCodeLength;
    }

}

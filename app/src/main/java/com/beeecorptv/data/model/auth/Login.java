package com.beeecorptv.data.model.auth;


import com.google.gson.annotations.SerializedName;

public class Login {


    @SerializedName("access_token")
    private String accessToken;


    @SerializedName("refresh_token")
    private String refresh;


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }


}

package com.beeecorptv.data.model.auth;

import com.beeecorptv.data.local.entity.Media;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserAuthInfo {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("name")
    @Expose
    private String name;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("provider_name")
    @Expose
    private String providerName;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @SerializedName("type")
    @Expose
    private String type;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @SerializedName("pack_name")
    @Expose
    private String packName;


    @SerializedName("pack_id")
    @Expose
    private String packId;


    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("transaction_id")
    @Expose
    private String transactionId;


    @SerializedName("start_at")
    @Expose
    private String startAt;

    @SerializedName("expired_in")
    @Expose
    private String expiredIn;

    @SerializedName("premuim")
    @Expose
    private Integer premuim;

    public Integer getManualPremuim() {
        return manualPremuim;
    }

    public void setManualPremuim(Integer manualPremuim) {
        this.manualPremuim = manualPremuim;
    }

    @SerializedName("manual_premuim")
    @Expose
    private Integer manualPremuim;


    @SerializedName("email_verified_at")
    @Expose
    private String emailVerifiedAt;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("message")
    @Expose
    private String message;


    public List<Media> getFavoritesAnimes() {
        return favoritesAnimes;
    }

    public void setFavoritesAnimes(List<Media> favoritesAnimes) {
        this.favoritesAnimes = favoritesAnimes;
    }

    @SerializedName("favoritesAnimes")
    @Expose
    private List<Media> favoritesAnimes = null;


    @SerializedName("favoritesSeries")
    @Expose
    private List<Media> favoritesSeries = null;


    public List<Media> getFavoritesStreaming() {
        return favoritesStreaming;
    }

    public void setFavoritesStreaming(List<Media> favoritesStreaming) {
        this.favoritesStreaming = favoritesStreaming;
    }

    @SerializedName("favoritesStreaming")
    @Expose
    private List<Media> favoritesStreaming = null;


    public List<Media> getFavoritesSeries() {
        return favoritesSeries;
    }

    public void setFavoritesSeries(List<Media> latest)
    {
        this.favoritesSeries = latest;
    }

    @SerializedName("favoritesMovies")
    @Expose
    private List<Media> favoritesMovies = null;


    public List<UserAuthInfo> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<UserAuthInfo> profiles) {
        this.profiles = profiles;
    }

    @SerializedName("profiles")
    @Expose
    private List<UserAuthInfo> profiles = null;


    public List<Media> getFavoritesMovies() {
        return favoritesMovies;
    }

    public void setFavoritesMovies(List<Media> latest) {
        this.favoritesMovies = latest;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getPremuim() {
        return premuim;
    }

    public void setPremuim(Integer premuim) {
        this.premuim = premuim;
    }

    public Object getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public void setEmailVerifiedAt(String emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }


    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getPackId() {
        return packId;
    }

    public void setPackId(String packId) {
        this.packId = packId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getExpiredIn() {
        return expiredIn;
    }

    public void setExpiredIn(String expiredIn) {
        this.expiredIn = expiredIn;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
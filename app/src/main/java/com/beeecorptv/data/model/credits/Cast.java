package com.beeecorptv.data.model.credits;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Yobex.
 */
public class Cast implements Parcelable {

    protected Cast(Parcel in) {
        globaldata = in.createTypedArrayList(Cast.CREATOR);
        castId = in.readInt();
        character = in.readString();
        creditId = in.readString();
        gender = in.readInt();
        id = in.readInt();
        name = in.readString();
        order = in.readInt();
        profilePath = in.readString();
        biography = in.readString();
        birthday = in.readString();
        placeOfBirth = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(globaldata);
        dest.writeInt(castId);
        dest.writeString(character);
        dest.writeString(creditId);
        dest.writeInt(gender);
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(order);
        dest.writeString(profilePath);
        dest.writeString(biography);
        dest.writeString(birthday);
        dest.writeString(placeOfBirth);
    }

    public String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Cast> CREATOR = new Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel in) {
            return new Cast(in);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };

    public List<Cast> getGlobaldata() {
        return globaldata;
    }

    public void setGlobaldata(List<Cast> globaldata) {
        this.globaldata = globaldata;
    }

    @SerializedName("data")
    @Expose
    private List<Cast> globaldata = null;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @SerializedName("type")
    private String type;

    @SerializedName("cast_id")
    private int castId;

    @SerializedName("character")
    private String character;

    @SerializedName("credit_id")
    private String creditId;

    @SerializedName("gender")
    private int gender;

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    @SerializedName("views")
    private int views;


    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("order")
    private int order;

    @SerializedName("profile_path")
    private String profilePath;


    @SerializedName("biography")
    private String biography;


    public String getBiography() {
        return biography;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @SerializedName("birthday")
    private String birthday;


    @SerializedName("place_of_birth")
    private String placeOfBirth;

    public Cast(int castId, String character, String creditId, int id, String name, int order, String profilePath) {
        this.castId = castId;
        this.character = character;
        this.creditId = creditId;
        this.id = id;
        this.name = name;
        this.order = order;
        this.profilePath = profilePath;
    }


    public int getCastId() {
        return castId;
    }

    public void setCastId(int castId) {
        this.castId = castId;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }


}
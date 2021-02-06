package com.example.rishonlovesanimals;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Profile implements Parcelable, Serializable {
    private String profile_name,profile_age,profile_id,profile_position,email;
    private String arriving;
    public Profile(String profile_name, String profile_age, String profile_id, String profile_position) {
        this.profile_name = profile_name;
        this.profile_age = profile_age;
        this.profile_id = profile_id;
        this.profile_position = profile_position;
    }
    public Profile(){}

    protected Profile(Parcel in) {
        profile_name = in.readString();
        profile_age = in.readString();
        profile_id = in.readString();
        profile_position = in.readString();
        email = in.readString();
        arriving = in.readString();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return email;
    }
    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getProfile_age() {
        return profile_age;
    }

    public void setProfile_age(String profile_age) {
        this.profile_age = profile_age;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public String getProfile_position() {
        return profile_position;
    }

    public void setProfile_position(String profile_position) {
        this.profile_position = profile_position;
    }

    public String getArriving() {
        return "arriving " + arriving;
    }

    public void setArriving(String arriving) {
        this.arriving = arriving;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(profile_name);
        dest.writeString(profile_age);
        dest.writeString(profile_id);
        dest.writeString(profile_position);
        dest.writeString(email);
        dest.writeString(arriving);
    }
}

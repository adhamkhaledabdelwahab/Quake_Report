package kh.ad.quakereport.model;

import com.google.gson.annotations.SerializedName;

public class Properties {
    @SerializedName("mag")
    private float mag;

    @SerializedName("place")
    private String place;

    @SerializedName("time")
    private long Time;

    public float getMag() {
        return mag;
    }

    public String getPlace() {
        return place;
    }

    public long getTime() {
        return Time;
    }

    public String getUrl() {
        return url;
    }

    @SerializedName("url")
    private String url;
}

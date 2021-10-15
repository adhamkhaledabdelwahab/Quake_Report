package kh.ad.quakereport.model;

import com.google.gson.annotations.SerializedName;

public class JsonModel {

    @SerializedName("features")
    private DataItem[] features;

    public DataItem[] getFeatures() {
        return features;
    }
}

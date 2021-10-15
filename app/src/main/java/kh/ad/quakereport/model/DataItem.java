package kh.ad.quakereport.model;

import com.google.gson.annotations.SerializedName;

public class DataItem {
    @SerializedName("properties")
    private Properties prop;

    public Properties getProp() {
        return prop;
    }
}

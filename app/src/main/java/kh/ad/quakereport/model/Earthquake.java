package kh.ad.quakereport.model;

public class Earthquake {

    float magnitude;
    String location;
    String date;
    String url;

    public Earthquake(float magnitude, String location, String date, String url) {
        this.magnitude = magnitude;
        this.location = location;
        this.date = date;
        this.url = url;
    }

    public float getMagnitude() {
        return magnitude;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }
}

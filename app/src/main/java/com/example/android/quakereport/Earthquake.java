package com.example.android.quakereport;

public class Earthquake {

    private Double mMagnitude;

    private String mLocation;

    private long mTimeInMS;

    private String mUrl;

    public Earthquake(Double magnitude, String location, long time, String url) {
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMS = time;
        mUrl = url;

    }

    public Double getMagnitude() { return mMagnitude;}

    public String getLocation() { return mLocation;}

    public long getTimeInMS() { return mTimeInMS;}

    public String getUrl() { return mUrl;}

}

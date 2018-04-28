package com.charles.geo.model;

/**
 * 一个经纬度点的封装
 *
 * @author huqj
 * @since 1.0
 */
public class GeoPoint {

    private double longitude;

    private double latitude;

    public double getDist(GeoPoint p) {
        return Math.sqrt(Math.pow(longitude - p.getLongitude(), 2) + Math.pow(latitude - p.getLatitude(), 2));
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}

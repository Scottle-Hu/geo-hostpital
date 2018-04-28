package com.charles.geo.model;

import java.util.List;

/**
 * 大学类
 *
 * @author Charles
 * @since 1.0
 */
public class Colleage {

    private String name;

    private String address;

    private String longitude;

    private String latitude;

    /**
     * 论文集合
     */
    private List<Paper> paperList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public List<Paper> getPaperList() {
        return paperList;
    }

    public void setPaperList(List<Paper> paperList) {
        this.paperList = paperList;
    }
}

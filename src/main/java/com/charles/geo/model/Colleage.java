package com.charles.geo.model;

import java.util.List;
import java.util.Map;

/**
 * 大学类
 *
 * @author Charles
 * @since 1.0
 */
public class Colleage {

    private String id;

    private String name;

    private String address;

    private String longitude;

    private String latitude;

    /**
     * 论文集合(每个map的键为疾病名称)
     */
    private Map<String, List<Paper>> paperList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Map<String, List<Paper>> getPaperList() {
        return paperList;
    }

    public void setPaperList(Map<String, List<Paper>> paperList) {
        this.paperList = paperList;
    }

    @Override
    public String toString() {
        return "Colleage{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", paperList=" + paperList +
                '}';
    }
}

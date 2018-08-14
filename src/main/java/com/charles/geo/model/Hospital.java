package com.charles.geo.model;

import com.charles.geo.utils.StringUtil;

import java.util.List;

/**
 * 医院对象，此对象是返回结果信息，不同于PointPlace中的医院
 *
 * @author huqj
 * @since 1.0
 */
public class Hospital {

    private String id;

    private String name;

    private String alias;

    private double longitude;

    private double latitude;

    private String quality;

    private String level;

    private String address;

    private String place;

    private String introduction;

    private String transport;

    private String url;

    /**
     * 专家医生列表
     */
    private List<Expert> expertList;

    private int score;

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
        this.name = StringUtil.clearStringFromWeb(name);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = StringUtil.clearStringFromWeb(alias);
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

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = StringUtil.clearStringFromWeb(quality);
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = StringUtil.clearStringFromWeb(level);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = StringUtil.clearStringFromWeb(introduction);
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = StringUtil.clearStringFromWeb(transport);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Expert> getExpertList() {
        return expertList;
    }

    public void setExpertList(List<Expert> expertList) {
        this.expertList = expertList;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Hospital{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", quality='" + quality + '\'' +
                ", level='" + level + '\'' +
                ", address='" + address + '\'' +
                ", place='" + place + '\'' +
                ", introduction='" + introduction + '\'' +
                ", transport='" + transport + '\'' +
                ", url='" + url + '\'' +
                ", expertList=" + expertList +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Hospital && ((Hospital) obj).getName().equals(this.name);
    }
}

package com.charles.geo.model;

import com.sun.org.apache.regexp.internal.RE;

import java.io.Serializable;
import java.util.List;

/**
 * 行政区划实体类
 *
 * @author huqj
 * @since 1.0
 */
public class Region implements Serializable {

    /**
     * 唯一id
     */
    private String id;

    /**
     * 标准名称
     */
    private String name;

    /**
     * 别名列表
     */
    private List<String> aliasList;

    /**
     * 经纬度（省级区划没有）
     */
    private String longitude;

    private String latitude;

    /**
     * 父行政区划【对象】
     */
    private String fatherId;

    /**
     * 行政区划级别：省、区县 等
     */
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAliasList() {
        return aliasList;
    }

    public void setAliasList(List<String> aliasList) {
        this.aliasList = aliasList;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Region) && ((Region) obj).getId().equals(this.id);
    }

    @Override
    public String toString() {
        return "Region{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", aliasList=" + aliasList +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", fatherId='" + fatherId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

package com.charles.geo.model;

/**
 * poi地点对象
 *
 * @author huqj
 * @since 1.0
 */
public class PointPlace {

    /**
     * id
     */
    private String id;

    /**
     * 经纬度
     */
    private String longitude;

    private String latitude;

    /**
     * name
     */
    private String name;

    /**
     * 所属行政区划（区县级别）
     */
    private String fatherId;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String father_id) {
        this.fatherId = father_id;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PointPlace) && ((PointPlace) obj).getId().equals(this.id);
    }

    @Override
    public String toString() {
        return "PointPlace{" +
                "id='" + id + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", name='" + name + '\'' +
                ", fatherId=" + fatherId +
                '}';
    }
}

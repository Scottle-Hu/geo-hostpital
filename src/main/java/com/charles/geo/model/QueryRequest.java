package com.charles.geo.model;

import java.util.List;

/**
 * 封装查询的各种信息：经纬度、行政区划、疾病名称
 *
 * @author huqj
 * @since 1.0
 */
public class QueryRequest {

    private List<GeoPoint> pointList;

    private List<Region> regionList;

    private List<Disease> diseaseList;

    /**
     * 查询者所处经纬度
     */
    private GeoPoint currentPoint;

    /**
     * 查询者ip
     */
    private String currentIP;

    /**
     * 用户唯一id
     */
    private String uid;

    public List<GeoPoint> getPointList() {
        return pointList;
    }

    public void setPointList(List<GeoPoint> pointList) {
        this.pointList = pointList;
    }

    public List<Region> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<Region> regionList) {
        this.regionList = regionList;
    }

    public List<Disease> getDiseaseList() {
        return diseaseList;
    }

    public void setDiseaseList(List<Disease> diseaseList) {
        this.diseaseList = diseaseList;
    }

    public GeoPoint getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(GeoPoint currentPoint) {
        this.currentPoint = currentPoint;
    }

    public String getCurrentIP() {
        return currentIP;
    }

    public void setCurrentIP(String currentIP) {
        this.currentIP = currentIP;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

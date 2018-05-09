package com.charles.geo.model;

import java.util.List;

/**
 * 返回的推荐数据封装
 *
 * @author Charles
 * @since 1.0
 */
public class InformationResponse {

    /**
     * 响应时长
     */
    private String responseTime;

    /**
     * 推荐医院
     */
    private List<Hospital> hospitalList;

    /**
     * 推荐大学
     */
    private List<Colleage> colleageList;

    public List<Hospital> getHospitalList() {
        return hospitalList;
    }

    public void setHospitalList(List<Hospital> hospitalList) {
        this.hospitalList = hospitalList;
    }

    public List<Colleage> getColleageList() {
        return colleageList;
    }

    public void setColleageList(List<Colleage> colleageList) {
        this.colleageList = colleageList;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }
}

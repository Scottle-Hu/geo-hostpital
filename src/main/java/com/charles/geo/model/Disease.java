package com.charles.geo.model;

import java.util.List;

/**
 * 疾病类
 *
 * @author Charles
 * @since 1.0
 */
public class Disease {

    private String name;

    private List<String> medicalList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMedicalList() {
        return medicalList;
    }

    public void setMedicalList(List<String> medicalList) {
        this.medicalList = medicalList;
    }
}

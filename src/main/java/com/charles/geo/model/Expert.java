package com.charles.geo.model;

import com.charles.geo.utils.StringUtil;

/**
 * 专家信息
 *
 * @author huqj
 * @since 1.0
 */
public class Expert {

    private String id;

    private String name;

    private String photo;

    private String department;

    private String major;

    private String level;

    private String url;

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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = StringUtil.clearStringFromWeb(major);
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = StringUtil.clearStringFromWeb(level);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Expert{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", department='" + department + '\'' +
                ", major='" + major + '\'' +
                ", level='" + level + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

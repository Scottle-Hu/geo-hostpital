package com.charles.geo.model;

import java.util.Date;

/**
 * 论文对象
 *
 * @author Cahrles
 * @since 1.0
 */
public class Paper {

    private String title;

    private Date publishTime;

    private String author;

    /**
     * 作者机构
     */
    private String org;

    /**
     * 来源
     */
    private String source;

    /**
     * 被引次数
     */
    private int reference;

    /**
     * 知网页面url
     */
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getReference() {
        return reference;
    }

    public void setReference(int reference) {
        this.reference = reference;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

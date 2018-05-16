package com.charles.geo.model;

import java.util.Date;

/**
 * 论文对象
 *
 * @author Cahrles
 * @since 1.0
 */
public class Paper {

    private String id;

    private String title;

    private String publishTime;

    private String author;

    /**
     * 下载次数
     */
    private int dnum;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getDnum() {
        return dnum;
    }

    public void setDnum(int dnum) {
        this.dnum = dnum;
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

    @Override
    public String toString() {
        return "Paper{" +
                "title='" + title + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", author='" + author + '\'' +
                ", dnum=" + dnum +
                ", source='" + source + '\'' +
                ", reference=" + reference +
                ", url='" + url + '\'' +
                '}';
    }
}

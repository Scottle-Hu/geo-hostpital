package com.charles.geo.service.std;

import com.charles.geo.model.*;

import java.util.List;

/**
 * 将ner出来的实体做标准化处理
 *
 * @author Cahrles
 * @since 1.0
 */
public interface IStdService {

    /**
     * 将一个地名（poi）转化为经纬度点
     *
     * @param place
     * @return
     */
    GeoPoint convertPOI2Point(String place);

    /**
     * 将药物名称转化为疾病对象集合
     *
     * @param medicine
     * @return
     */
    List<Disease> convertMedicine2Disease(String medicine);

    /**
     * 将识别出的行政区划名称和地点名称转化为标准名称并封装成对象
     *
     * @param regionNames 行政区划名称集合
     * @param pointNames  地点名称集合
     * @param regionList  行政区划对象列表
     * @param pointList   经纬度点对象集合
     */
    void convert2StdName(List<String> regionNames, List<String> pointNames,
                         List<Region> regionList, List<GeoPoint> pointList);

    /**
     * 将疾病名称标准化并转化为疾病对象集合
     *
     * @param diseaseNames
     * @return
     */
    List<Disease> stdDisease(List<String> diseaseNames);

    /**
     * 将医疗专家的姓名转化为医院集合（因为可能存在重名）
     *
     * @param name
     * @return
     */
    List<Hospital> convertPeople2Hospital(String name);
}

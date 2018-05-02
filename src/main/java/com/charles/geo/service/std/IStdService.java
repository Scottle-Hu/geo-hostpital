package com.charles.geo.service.std;

import com.charles.geo.model.GeoPoint;

/**
 * 将ner出来的实体做标准化处理，可供nerService调用
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


    //#############下面是对别名、歧义问题的处理方法##############

}

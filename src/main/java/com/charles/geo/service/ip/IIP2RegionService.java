package com.charles.geo.service.ip;

/**
 * 根据ip获取客户端所处行政区划的方法
 *
 * @author huqj
 */
public interface IIP2RegionService {

    String getCityFromIP(String ip);

}

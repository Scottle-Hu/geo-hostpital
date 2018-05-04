package com.charles.geo.service.std.impl;

import com.charles.geo.mapper.DiseaseMapper;
import com.charles.geo.mapper.HospitalMapper;
import com.charles.geo.mapper.PlaceMapper;
import com.charles.geo.mapper.UniversityMapper;
import com.charles.geo.model.*;
import com.charles.geo.service.std.IStdService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @author huqj
 * @sin 1.0
 */
@Service("stdService")
public class StdServiceImpl implements IStdService {

    /**
     * 日志记录器
     */
    private Logger LOGGER = Logger.getLogger(StdServiceImpl.class);

    private final String apiUrl = "http://api.map.baidu.com/geocoder/v2/?address=(?)&output=json" +
            "&ak=GpTMhCzysDnj632F3x14G8QbOBGyuMKr";

    @Autowired
    private DiseaseMapper diseaseMapper;

    @Autowired
    private HospitalMapper hospitalMapper;

    @Autowired
    private PlaceMapper placeMapper;

    @Autowired
    private UniversityMapper universityMapper;

    public GeoPoint convertPOI2Point(String place) {
        return null;
    }

    public List<Disease> convertMedicine2Disease(String medicine) {
        return null;
    }

    public void convert2StdName(List<String> regionNames, List<String> pointNames,
                                List<Region> regionList, List<GeoPoint> pointList) {
        //行政区划别名识别直接参照t_alias
        Set<String> regionIds = new HashSet<String>();
        for (String region : regionNames) {
            List<String> idList = placeMapper.findPlaceIdByAlias(region);
            regionIds.addAll(idList);
        }
        Iterator<String> iterator = pointNames.iterator();
        while (iterator.hasNext()) {
            String point = iterator.next();
            List<String> idList = placeMapper.findPlaceIdByAlias(point);
            if (idList != null && idList.size() > 0) {
                iterator.remove();
            }
            regionIds.addAll(idList);
        }
        for (String id : regionIds) {
            regionList.add(placeMapper.findRegionById(id));
        }
        /*
        地点转化为经纬度的优先级：
        先查大学、医院，只因为这两张表比较快
        然后分别精确和模糊查询t_place表【耗时】
        最后调用api查询【最耗时，且不确定性较大】
         */
        for (String name : pointNames) {
            //尝试查询大学标准名称
            Colleage colleage = universityMapper.queryByName(name);
            if (colleage != null) {
                GeoPoint geoPoint = new GeoPoint();
                geoPoint.setLongitude(Double.parseDouble(colleage.getLongitude()));
                geoPoint.setLatitude(Double.parseDouble(colleage.getLatitude()));
                pointList.add(geoPoint);
                continue;
            }
            //尝试查询大学简称
            List<Colleage> colleageList = universityMapper.queryByAliases(name);
            if (colleageList != null && colleageList.size() > 0) {
                for (Colleage c : colleageList) {
                    GeoPoint geoPoint = new GeoPoint();
                    geoPoint.setLongitude(Double.parseDouble(c.getLongitude()));
                    geoPoint.setLatitude(Double.parseDouble(c.getLatitude()));
                    pointList.add(geoPoint);
                }
                continue;
            }
            //尝试查询医院名称
            List<Hospital> hospitals = hospitalMapper.queryByName(name);
            if (hospitals != null && hospitals.size() > 0) {
                for (Hospital h : hospitals) {
                    GeoPoint p = new GeoPoint();
                    p.setLatitude(h.getLatitude());
                    p.setLongitude(h.getLongitude());
                    pointList.add(p);
                }
                continue;
            }
            //尝试查询医院别名
//            hospitals = hospitalMapper.queryByAlias(name);
//            if (hospitals != null && hospitals.size() > 0) {
//                for (Hospital h : hospitals) {
//                    GeoPoint p = new GeoPoint();
//                    p.setLatitude(h.getLatitude());
//                    p.setLongitude(h.getLongitude());
//                    pointList.add(p);
//                }
//                continue;
//            }
            //尝试查询t_place地点数据
//            List<PointPlace> pointPlaces = placeMapper.queryPointPlaceByName(name);
//            if (pointPlaces != null && pointPlaces.size() > 0) {
//                for (PointPlace place : pointPlaces) {
//                    GeoPoint p = new GeoPoint();
//                    p.setLatitude(Double.parseDouble(place.getLatitude()));
//                    p.setLongitude(Double.parseDouble(place.getLongitude()));
//                    pointList.add(p);
//                }
//                continue;
//            }
//            pointPlaces = placeMapper.queryPointPlaceByName(name + "%");
//            if (pointPlaces != null && pointPlaces.size() > 0) {
//                for (PointPlace place : pointPlaces) {
//                    GeoPoint p = new GeoPoint();
//                    p.setLatitude(Double.parseDouble(place.getLatitude()));
//                    p.setLongitude(Double.parseDouble(place.getLongitude()));
//                    pointList.add(p);
//                }
//                continue;
//            }
            //尝试调用api
            getGeoPointByAPI(name, regionList, pointList);
        }

    }

    public void getGeoPointByAPI(String name, List<Region> regionList, List<GeoPoint> pointList) {
        int maxConfidence = 0;
        GeoPoint tmp = new GeoPoint();
        for (Region region : regionList) {
            try {
                String name2 = region.getName() + name;
                String api = getAPiUrl(name2);
                LOGGER.info("调用api查询经纬度：" + name2);
                String result = getContent(api);
                System.out.println(result);
                int statuStart = result.indexOf("status\":");
                if (statuStart != -1) {
                    int statuEnd = result.indexOf(",", statuStart);
                    if (statuEnd != -1) {
                        String statu = result.substring(statuStart + 8, statuEnd);
                        if (statu.equals("0")) {
                            int confidenceStart = result.indexOf("confidence\":");
                            int confidenceEnd = result.indexOf(",", confidenceStart);
                            int confidence = Integer.parseInt(result.substring(confidenceStart + 12, confidenceEnd));
                            if (confidence > maxConfidence) {
                                maxConfidence = confidence;
                                int lngStart = result.indexOf("lng\":");
                                int lngEnd = result.indexOf(",", lngStart);
                                double lng = Double.parseDouble(result.substring(lngStart + 5, lngEnd));
                                int latStart = result.indexOf("lat\":");
                                int latEnd = result.indexOf("}", latStart);
                                double lat = Double.parseDouble(result.substring(latStart + 5, latEnd));
                                tmp.setLatitude(lat);
                                tmp.setLongitude(lng);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("api查询过程出错");
            }
        }
        pointList.add(tmp);
    }

    private String getAPiUrl(String name) {
        return apiUrl.replace("(?)", name);
    }

    /**
     * 通过url获取内容的封装方法
     *
     * @param url
     * @return
     */
    private String getContent(String url) {
        HttpClient client = new DefaultHttpClient();
        //构造一级请求的url
        HttpGet getHttp = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = client.execute(getHttp);
        } catch (IOException e) {
            LOGGER.error("使用httpclient抓取页面内容出现问题");
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        String content = null;
        try {
            content = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭资源
        client.getConnectionManager().shutdown();
        return content;
    }

    /**
     * 将疾病名称标准化，转化为疾病对象集合
     *
     * @param diseaseNames
     * @return
     */
    public List<Disease> stdDisease(List<String> diseaseNames) {
        List<Disease> diseaseList = new ArrayList<Disease>();
        for (String disease : diseaseNames) {
            //先尝试按照标准名称获取疾病
            List<Disease> diseases = diseaseMapper.findByName(disease);
            if (diseases == null || diseases.size() == 0) {
                //尝试模糊匹配
                diseases = diseaseMapper.findByName("%" + disease + "%");
            }
            Set<String> names = new HashSet<String>();
            //去重，对于多个科室对应的疾病只保留一个(后面计算相关性需要用到科室信息的时候再取)
            for (Disease d : diseases) {
                if (names.contains(d.getName())) {
                    continue;
                }
                names.add(d.getName());
                diseaseList.add(d);
            }
        }
        return diseaseList;
    }

    /**
     * 将专家名称转化为经纬度点的集合
     *
     * @param name
     * @return
     */
    public List<GeoPoint> convertPeople2Hospital(String name) {
        List<GeoPoint> points = new ArrayList<GeoPoint>();
        List<Hospital> hospitals = hospitalMapper.queryByExpert(name);
        for (Hospital h : hospitals) {
            double lng = h.getLongitude();
            double lat = h.getLatitude();
            GeoPoint p = new GeoPoint();
            p.setLatitude(lat);
            p.setLongitude(lng);
            points.add(p);
        }
        return points;
    }
}

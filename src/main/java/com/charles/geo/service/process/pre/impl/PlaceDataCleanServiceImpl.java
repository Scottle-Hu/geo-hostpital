package com.charles.geo.service.process.pre.impl;

import com.charles.geo.mapper.PlaceMapper;
import com.charles.geo.model.GeoPoint;
import com.charles.geo.model.Region;
import com.charles.geo.service.process.pre.IPlaceDataCleanService;
import com.charles.geo.utils.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 预处理地点数据，排除异常
 *
 * @author huqj
 * @since 1.0
 */
@Service("placeDataCleanService")
public class PlaceDataCleanServiceImpl implements IPlaceDataCleanService {

    /**
     * k-means聚类的最大迭代次数
     */
    private int K_MEANS_MAX = 10;

    @Autowired
    private PlaceMapper placeMapper;

    /**
     * <h3>处理算法</h3>
     * <ol>
     * <li>处理所有有包含关系的行政区划，取最低层级的行政区划并保留。
     * 如果最低层级行政区有经纬度信息，则转化为一个聚类点，否则直接保留在regionList中</li>
     * <li>计算所有poi点的平均距离（以经纬度作为单位即可）</li>
     * <li>以第②步骤中计算出的平均距离作为T1,平均距离的一半作为T2进行canopy_k-means聚类</li>
     * <li>排除元素个数小于2的聚类</li>
     * <li>将得到的geoPoint和regionList保留</li>
     * </ol>
     *
     * @param geoPointList 经纬度列表
     * @param regionList   行政区划列表
     */
    public void clean(List<GeoPoint> geoPointList, List<Region> regionList) {
        Set<Region> mustSave = new HashSet<Region>();
        Set<Region> toRemove = new HashSet<Region>();
        for (Region region : regionList) {
            Set<Region> ancestors = allAncestor(region);
            for (Region r : regionList) {
                boolean f = false;
                for (Region rg : ancestors) {
                    if (rg.getId().equals(r.getId())) {
                        f = true;
                        break;
                    }
                }
                if (f) {
                    mustSave.add(region);
                    if (mustSave.contains(r)) {
                        mustSave.remove(r);
                    }
                    toRemove.add(r);
                }
            }
        }
        mustSave.removeAll(toRemove);
        regionList.removeAll(toRemove);
        //找能够转化为经纬度的行政区
        Map<GeoPoint, Region> map = new HashMap<GeoPoint, Region>();
        for (Region region : regionList) {
            String longitude = region.getLongitude();
            String latitude = region.getLatitude();
            if (StringUtils.isBlank(longitude) || StringUtils.isBlank(latitude)) {
                continue;
            }
            GeoPoint p = new GeoPoint();
            try {
                p.setLatitude(Double.parseDouble(latitude));
                p.setLongitude(Double.parseDouble(longitude));
                geoPointList.add(p);
                map.put(p, region);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //对poi点进行canopy聚类
        Map<GeoPoint, Set<GeoPoint>> canopyRes = canopyForPOI(geoPointList);
        //对poi点进行k-means聚类并排除离散的点
        kmeansForPOI(geoPointList, canopyRes);
        //还原行政区划，去除行政区划转化的poi点
        Iterator<GeoPoint> iterator = geoPointList.iterator();
        while (iterator.hasNext()) {
            GeoPoint p = iterator.next();
            Region r = map.get(p);
            if (r != null) {
                mustSave.add(r);
                iterator.remove();
            }
        }
        regionList.clear();
        regionList.addAll(mustSave);
    }

    /**
     * 找到一个行政区划的所有上级集合
     *
     * @param region
     * @return 所有上级
     */
    public Set<Region> allAncestor(Region region) {
        Set<Region> res = new HashSet<Region>();
        String fatherId = region.getFatherId();
        while (!StringUtils.isBlank(fatherId)) {
            //TODO 此处频繁查数据库，后期可做优化
            Region r = placeMapper.findRegionById(fatherId);
            if (r == null) {
                break;
            }
            res.add(r);
            fatherId = r.getFatherId();
        }
        return res;
    }

    /**
     * 对poi点进行canopy聚类
     *
     * @param points2
     */
    public Map<GeoPoint, Set<GeoPoint>> canopyForPOI(List<GeoPoint> points2) {
        //copy，否则导致bug
        List<GeoPoint> points = new ArrayList<GeoPoint>();
        points.addAll(points2);
        //获取参数
        double T1 = getT1ForCanopy(points) / 2;
        double T2 = T1 / 2;
        //开始迭代
        Map<GeoPoint, Set<GeoPoint>> cluster = new HashMap<GeoPoint, Set<GeoPoint>>();
        int size = points.size();
        while (size > 0) {
            int randomIndex = 0;  //(int) (Math.random() * points.size());
            GeoPoint center = points.get(randomIndex);
            points.remove(randomIndex);
            Set<GeoPoint> list = new HashSet<GeoPoint>();
            list.add(center);
            Iterator<GeoPoint> ite = points.iterator();
            while (ite.hasNext()) {
                GeoPoint p = ite.next();
                if (center.getDist(p) < T1) {
                    list.add(p);
                }
                if (center.getDist(p) < T2) {
                    ite.remove();
                }
            }
            cluster.put(center, list);
            size = points.size();
        }
        //test
        System.out.println("canopy聚类结果:" + cluster);
        return cluster;
    }


    /**
     * 对canopy聚类的结果做k-means聚类
     * 并排除离散点
     *
     * @param points
     * @param cluster
     */
    public void kmeansForPOI(List<GeoPoint> points, Map<GeoPoint, Set<GeoPoint>> cluster) {
        //规模较小，倒排 元素->中心 可优化速度
        Map<GeoPoint, Set<GeoPoint>> center = new HashMap<GeoPoint, Set<GeoPoint>>();
        for (Map.Entry<GeoPoint, Set<GeoPoint>> e : cluster.entrySet()) {
            for (GeoPoint p : e.getValue()) {
                Set<GeoPoint> list = center.get(p);
                if (list == null) {
                    list = new HashSet<GeoPoint>();
                    list.add(e.getKey());
                    center.put(p, list);
                } else {
                    list.add(e.getKey());
                }
            }
        }
        //初始化聚类结果map，开始只把中心添加进去
        Map<GeoPoint, Set<GeoPoint>> core = new HashMap<GeoPoint, Set<GeoPoint>>();
        for (Map.Entry<GeoPoint, Set<GeoPoint>> e : cluster.entrySet()) {
            core.put(e.getKey(), new HashSet<GeoPoint>());
            core.get(e.getKey()).add(e.getKey());
        }
        int itNum = 0;  //迭代次数
        boolean centerChanged = true;
        while (itNum < K_MEANS_MAX && centerChanged) {
            itNum++;
            for (GeoPoint p : points) {
                double minDis = Double.MAX_VALUE;
                GeoPoint newCenter = null;
                for (GeoPoint c : center.get(p)) {
                    double dis = p.getDist(c);
                    if (minDis > dis) {
                        minDis = dis;
                        newCenter = c;
                    }
                }
                //TODO 此处逻辑太多，后续可拆分成方法
                if (newCenter != null) {
                    //从原先聚类移除
                    GeoPoint oldCenter = null;
                    boolean found = false;
                    for (Map.Entry<GeoPoint, Set<GeoPoint>> e : core.entrySet()) {
                        for (GeoPoint p2 : e.getValue()) {
                            if (p2.equals(p)) {
                                oldCenter = e.getKey();
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            break;
                        }
                    }
                    if (oldCenter != null) {
                        core.get(oldCenter).remove(p);
                    }
                    //添加到新聚类中
                    core.get(newCenter).add(p);
                }
            }
            //更新每个聚类的聚簇中心，记录有没有发生变化
            centerChanged = reCalCenter(core, center);
        }
        //test
        System.out.println("k-means聚类结果:" + core);
        //去除离散的点
        int aveSzie = 0;
        for (Map.Entry<GeoPoint, Set<GeoPoint>> e : core.entrySet()) {
            aveSzie += e.getValue().size();
        }
        aveSzie /= core.keySet().size();
        for (Map.Entry<GeoPoint, Set<GeoPoint>> e : core.entrySet()) {
            if (e.getValue().size() <= 1) {
                points.removeAll(e.getValue());
            }
        }
    }

    /**
     * 计算canopy聚类的参数
     *
     * @param points
     * @return
     */
    public double getT1ForCanopy(List<GeoPoint> points) {
        double res = 0;
        int size = points.size();
        for (int i = 0; i < size; i++) {
            GeoPoint p1 = points.get(i);
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    continue;
                }
                GeoPoint p2 = points.get(j);
                res += p1.getDist(p2);
            }
        }
        return res / (size * (size - 1) / 2);
    }

    /**
     * 判断聚簇中心有没有发生变化
     *
     * @param core   聚类
     * @param center 元素->需要比较的点 的倒排map
     * @return 是否变化
     */
    public boolean reCalCenter(Map<GeoPoint, Set<GeoPoint>> core, Map<GeoPoint, Set<GeoPoint>> center) {
        boolean res = false;
        Map<GeoPoint, GeoPoint> toChange = new HashMap<GeoPoint, GeoPoint>();
        for (Map.Entry<GeoPoint, Set<GeoPoint>> e : core.entrySet()) {
            int size = e.getValue().size();
            double totalLon = 0;
            double totalLat = 0;
            for (GeoPoint p : e.getValue()) {
                totalLat += p.getLatitude();
                totalLon += p.getLongitude();
            }
            GeoPoint newPoint = new GeoPoint();
            newPoint.setLongitude(totalLon / size);
            newPoint.setLatitude(totalLat / size);
            if (newPoint.getDist(e.getKey()) > Constant.DOUBLE_ZERO) {  //中心发生变化
                res = true;
                toChange.put(e.getKey(), newPoint);  //后面替换
                //更新倒排map
                for (GeoPoint gp : e.getValue()) {
                    center.get(gp).remove(e.getKey());
                    center.get(gp).add(newPoint);
                }
            }
        }
        for (Map.Entry<GeoPoint, GeoPoint> e : toChange.entrySet()) {
            GeoPoint oldCenter = e.getKey();
            GeoPoint newPoint = e.getValue();
            core.put(newPoint, core.get(oldCenter));
            core.remove(oldCenter);
        }
        return res;
    }

}

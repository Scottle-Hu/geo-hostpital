package com.charles.geo.service.rank;

import com.charles.geo.model.Colleage;
import com.charles.geo.model.Hospital;
import com.charles.geo.model.QueryRequest;

import java.util.List;

/**
 * 给推荐结果排序
 *
 * @author huqj
 * @since 1.0
 */
public interface IRankService {

    /**
     * @param hospitals 待排序医院列表
     * @param request   请求的封装，包含了疾病信息
     * @return 排序并筛选后的医院列表
     */
    List<Hospital> rankHospital(List<Hospital> hospitals, QueryRequest request);

    /**
     * @param colleages 待排序大学列表（包含每种疾病对应的论文列表）
     * @param request   请求的封装，包含了疾病信息
     * @return 排序筛选后的大学列表
     */
    List<Colleage> rankColleage(List<Colleage> colleages, QueryRequest request);

}

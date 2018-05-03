package com.charles.geo.service.process;

import com.charles.geo.model.InformationResponse;
import com.charles.geo.model.QueryRequest;

/**
 * 主功能服务，根据query对象推荐信息
 *
 * @author Charles
 * @since 1.0
 */
public interface IMainQueryService {

    /**
     * 根据请求的查询，推荐相关信息并封装
     *
     * @param request
     * @return
     */
    InformationResponse handler(QueryRequest request);

}

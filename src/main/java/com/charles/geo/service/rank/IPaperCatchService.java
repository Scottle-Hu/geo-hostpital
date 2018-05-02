package com.charles.geo.service.rank;

import com.charles.geo.model.Paper;

import java.util.List;

/**
 * 根据大学名称和疾病名称获取论文列表的函数
 *
 * @author huqj
 * @since 1.0
 */
public interface IPaperCatchService {

    /**
     * 根据大学名称和疾病名称抓取论文列表
     *
     * @param colleage
     * @param disease
     * @return
     */
    List<Paper> catchPaper(String colleage, String disease);

    /**
     * 根据大学名称和疾病名称从缓存中获取或者抓取论文列表
     *
     * @param colleage
     * @param disease
     * @return
     */
    List<Paper> getPaper(String colleage, String disease);

}

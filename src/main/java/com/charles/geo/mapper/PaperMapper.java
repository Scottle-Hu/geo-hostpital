package com.charles.geo.mapper;

import com.charles.geo.model.Paper;

import java.util.List;
import java.util.Map;

/**
 * @author huqj
 */
public interface PaperMapper {

    /**
     * 存入一条论文记录
     *
     * @param paper
     * @return
     */
    int insertOne(Paper paper);

    /**
     * 根据学校名称和疾病名称获取论文
     *
     * @param map
     * @return
     */
    List<Paper> findByColleage(Map<String, String> map);

    /**
     * 根据论文名称和作者信息判断某篇论文是否存在
     *
     * @return
     */
    List<Paper> findByNameAndAuthor(Map<String, String> map);

    /**
     * 向学校、疾病、论文关系表中插入一条记录
     *
     * @param map
     * @return
     */
    int insertUniversityAndDiseaseWithPaper(Map<String, String> map);

}

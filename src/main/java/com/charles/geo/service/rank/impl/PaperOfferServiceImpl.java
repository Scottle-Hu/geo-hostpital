package com.charles.geo.service.rank.impl;

import com.charles.geo.mapper.PaperMapper;
import com.charles.geo.model.Paper;
import com.charles.geo.service.rank.IPaperOfferService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author huqj
 */
@Service("paperOfferService")
public class PaperOfferServiceImpl implements IPaperOfferService {

    /**
     * 日志记录器
     */
    private Logger LOGGER = Logger.getLogger(PaperOfferServiceImpl.class);

    private String SEG = "|";

    /**
     * 15天清理过期缓存
     */
    private long intervalTime = 15 * 24 * 60 * 60 * 1000;

    /**
     * 存储大学-疾病的论文缓存，存储的key为大学|疾病
     */
    private Map<String, List<Paper>> paperCache = new HashMap<String, List<Paper>>();

    @Autowired
    private PaperMapper paperMapper;

    /**
     * 初始化方法定时清理过期缓存
     */
    @PostConstruct
    public void init() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                reload();
            }
        }, 0, intervalTime);
    }

    private void reload() {
        //TODO 只清理过期缓存即可，需要修改缓存结构
        paperCache.clear();
        LOGGER.info("清理过期论文缓存结束");
    }

    public List<Paper> getPaperByUniversityAndDisease(String university, String disease) {
        String key = university + SEG + disease;
        List<Paper> p = paperCache.get(key);
        if (p != null) {
            LOGGER.info("从缓存中获取数据：" + key);
            return p;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("university", university);
        map.put("disease", disease);
        List<String> ids = paperMapper.findPaperId(map);
        Set<String> distinctIds = new HashSet<String>();  //id去重
        distinctIds.addAll(ids);
        List<Paper> result = new ArrayList<Paper>();
        for (String id : distinctIds) {
            result.add(paperMapper.findByPaperId(id));
        }
        if (result.size() != 0) {
            paperCache.put(key, result);
            LOGGER.info("写入缓存：" + key);
        }
        return result;
    }
}

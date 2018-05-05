package com.charles.geo.service.rank.impl;

import com.charles.geo.model.Colleage;
import com.charles.geo.model.Hospital;
import com.charles.geo.model.Paper;
import com.charles.geo.model.QueryRequest;
import com.charles.geo.service.rank.IRankService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author huqj
 * @since 1.0
 */
@Service("rankServiceImpl")
public class RankServiceImpl implements IRankService {

    public List<Hospital> rankHospital(List<Hospital> hospitals, QueryRequest request) {
        Collections.sort(hospitals, new Comparator<Hospital>() {
            public int compare(Hospital o1, Hospital o2) {
                //比较挫的比较方法，先应付一下
                int s1 = 0;
                int s2 = 0;
                String v1 = o1.getLevel();
                String v2 = o2.getLevel();
                if (v1.contains("三级")) {
                    s1 += 30;
                } else if (v1.contains("二级")) {
                    s1 += 20;
                } else {
                    s1 += 10;
                }
                if (v1.contains("甲等")) {
                    s1 += 10;
                } else if (v1.contains("乙等")) {
                    s1 += 6;
                } else {
                    s1 += 3;
                }
                if (v1.contains("特等")) {
                    s1 += 10;
                }
                if (v2.contains("三级")) {
                    s2 += 30;
                } else if (v2.contains("二级")) {
                    s2 += 20;
                } else {
                    s2 += 10;
                }
                if (v2.contains("甲等")) {
                    s2 += 10;
                } else if (v2.contains("乙等")) {
                    s2 += 6;
                } else {
                    s2 += 3;
                }
                if (v2.contains("特等")) {
                    s2 += 10;
                }
                return s2 - s1;
            }
        });
        return hospitals;
    }

    public List<Colleage> rankColleage(List<Colleage> colleages, QueryRequest request) {
        //大学根据论文被引和下载次数排序
        Collections.sort(colleages, new Comparator<Colleage>() {
            public int compare(Colleage c1, Colleage c2) {
                Map<String, List<Paper>> p1 = c1.getPaperList();
                Map<String, List<Paper>> p2 = c2.getPaperList();
                if (p1 == null || p2 == null || p1.size() == 0) {
                    return 0;
                }
                int s1 = 0;
                int s2 = 0;
                for (Map.Entry<String, List<Paper>> e : p1.entrySet()) {
                    for (Paper p : e.getValue()) {
                        s1 += p.getReference() * 7 + p.getDnum() * 3;
                    }
                }
                for (Map.Entry<String, List<Paper>> e : p2.entrySet()) {
                    for (Paper p : e.getValue()) {
                        s2 += p.getReference() * 7 + p.getDnum() * 3;
                    }
                }
                return s2 - s1;
            }
        });
        return colleages;
    }
}

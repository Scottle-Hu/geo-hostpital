package com.charles.geo.service.rank.impl;

import com.charles.geo.mapper.DiseaseMapper;
import com.charles.geo.model.*;
import com.charles.geo.service.rank.IRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author huqj
 * @since 1.0
 */
@Service("rankService")
public class RankServiceImpl implements IRankService {

    @Autowired
    private DiseaseMapper diseaseMapper;

    public List<Hospital> rankHospital(List<Hospital> hospitals, final QueryRequest request) {
        for (Hospital h : hospitals) {
            calHospitalScore(h, request);
        }
        Collections.sort(hospitals, new Comparator<Hospital>() {
            public int compare(Hospital o1, Hospital o2) {
                return o2.getScore() - o1.getScore();
            }
        });
        return hospitals;
    }

    public List<Colleage> rankColleage(List<Colleage> colleages, QueryRequest request) {
        for (Colleage c : colleages) {
            calUniversityScore(c, request);
        }
        Collections.sort(colleages, new Comparator<Colleage>() {
            public int compare(Colleage c1, Colleage c2) {
                return c2.getScore() - c1.getScore();
            }
        });
        return colleages;
    }

    private void calHospitalScore(Hospital hospital, QueryRequest request) {
        int s1 = 0;
        String v1 = hospital.getLevel();
        if (v1 != null) {
            if (v1.contains("三级")) {
                s1 += 60;
            } else if (v1.contains("二级")) {
                s1 += 30;
            } else {
                s1 += 0;
            }
            if (v1.contains("甲等")) {
                s1 += 30;
            } else if (v1.contains("乙等")) {
                s1 += 20;
            } else if (v1.contains("丙等")) {
                s1 += 10;
            } else if (v1.contains("特等")) {
                s1 += 40;
            }
            if (v1.contains("未知")) {
                s1 += 10;
            }
        }
        s1 *= 26;
        int s2 = 0, s3 = 0;
        //根据科室计算得分
        List<Disease> diseaseList = request.getDiseaseList();
        Set<String> departList = new HashSet<String>();
        for (Disease d : diseaseList) {
            List<Disease> departs = diseaseMapper.findByName(d.getName());
            for (Disease k : departs) {
                String keshi = k.getDepart();
                if (!departList.contains(keshi)) {
                    departList.add(keshi);
                }
            }
        }
        Set<String> ds = new HashSet<String>();
        if (hospital.getExpertList() != null) {
            for (Expert expert : hospital.getExpertList()) {
                if (departList.contains(expert.getDepartment())) {
                    s2++;
                    if (!ds.contains(expert.getDepartment())) {
                        s3++;
                        ds.add(expert.getDepartment());
                    }
                }
            }
        }
        s2 *= 22;
        s3 *= 23;
        hospital.setScore(s1 + s2 + s3);
    }

    //大学根据论文被引和下载次数排序
    private void calUniversityScore(Colleage c, QueryRequest request) {
        if (c.getPaperList() == null || c.getPaperList().size() == 0) {
            c.setScore(0);
        }
        int s1 = 0;
        for (Map.Entry<String, List<Paper>> e : c.getPaperList().entrySet()) {
            for (Paper p : e.getValue()) {
                s1 += p.getReference() * 7 + p.getDnum() * 3;
            }
        }
        c.setScore(s1);
    }

}

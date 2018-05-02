package com.charles.geo.service.rank.impl;

import com.charles.geo.model.Colleage;
import com.charles.geo.model.Hospital;
import com.charles.geo.model.QueryRequest;
import com.charles.geo.service.rank.IRankService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author huqj
 * @since 1.0
 */
@Service("rankServiceImpl")
public class RankServiceImpl implements IRankService {
    
    public List<Hospital> rankHospital(List<Hospital> hospitals, QueryRequest request) {
        return null;
    }

    public List<Colleage> rankColleage(List<Colleage> colleages, QueryRequest request) {
        return null;
    }
}

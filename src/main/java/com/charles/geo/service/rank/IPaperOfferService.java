package com.charles.geo.service.rank;

import com.charles.geo.model.Paper;

import java.util.List;

/**
 * @author huqj
 */
public interface IPaperOfferService {

    List<Paper> getPaperByUniversityAndDisease(String university, String disease);
}

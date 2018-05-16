package com;

import com.charles.geo.mapper.DiseaseMapper;
import com.charles.geo.mapper.PaperMapper;
import com.charles.geo.mapper.UniversityMapper;
import com.charles.geo.model.Colleage;
import com.charles.geo.model.Paper;
import com.charles.geo.service.rank.IPaperCatchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * @author huqj
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext.xml"})
public class CatchPaperTest {

    @Autowired
    private IPaperCatchService paperCatchService;

    @Autowired
    private UniversityMapper universityMapper;

    @Autowired
    private DiseaseMapper diseaseMapper;

    @Autowired
    private PaperMapper paperMapper;

    /**
     * 静态抓取论文并存入数据库,之后可以视效果作为定时方法执行
     */
    @Test
    public void test01() {
        List<Colleage> uns = universityMapper.getAllName();
        List<String> ds = diseaseMapper.getAllName();
        int index = 0;
        int relationId = 0;
        for (Colleage university : uns) {
            System.out.println(++index + university.getName());
            int index2 = 0;
            for (String disease : ds) {
                index2++;
                int index3 = 0;
                try {
                    List<Paper> paper = paperCatchService.getPaper(university.getName(), disease);

                    for (Paper p : paper) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("title", p.getTitle());
                        map.put("author", p.getAuthor());
                        List<Paper> byNameAndAuthor = paperMapper.findByNameAndAuthor(map);
                        String paperId = null;
                        if (byNameAndAuthor != null && byNameAndAuthor.size() > 0) {
                            paperId = byNameAndAuthor.get(0).getId();
                        } else {
                            p.setId(index + "-" + index2 + "-" + (++index3));
                            paperId = p.getId();
                            paperMapper.insertOne(p);
                        }
                        Map<String, String> param = new HashMap<String, String>();
                        param.put("university", university.getName());
                        param.put("disease", disease);
                        param.put("paper_id", paperId);
                        param.put("id", ++relationId + "");
                        paperMapper.insertUniversityAndDiseaseWithPaper(param);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("一次论文存储失败");
                }

            }
        }

    }

}

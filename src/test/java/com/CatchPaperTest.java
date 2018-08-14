package com;

import com.charles.geo.mapper.DiseaseMapper;
import com.charles.geo.mapper.PaperMapper;
import com.charles.geo.mapper.UniversityMapper;
import com.charles.geo.model.Colleage;
import com.charles.geo.model.Paper;
import com.charles.geo.service.rank.IPaperCatchService;
import com.charles.geo.utils.IDUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import java.io.*;
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
        int relationId = 221240;
        for (Colleage university : uns) {
            System.out.println(++index + university.getName());
            if (index < 328) {
                continue;
            }
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

    /**
     * 抓取985高校论文
     */
    @Test
    public void test02() throws IOException {
        List<Colleage> uns = new ArrayList<Colleage>();
        File f = new File("D:\\JAVA\\intellij\\geo-hostpital\\src\\main\\resources\\985.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line = null;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("\uFEFF") || line.startsWith("\ufeff")) {
                line = line.substring(1, line.length());
            }
            Colleage c = new Colleage();
            c.setName(line);
            uns.add(c);
        }
        fr.close();
        List<String> ds = diseaseMapper.getAllName();
        int index = 0;
        int relationId = 321240;
        for (Colleage university : uns) {
            System.out.println(++index + university.getName());
            for (String disease : ds) {
                try {
                    Map<String, String> m = new HashMap<String, String>();
                    List<String> paperId1 = paperMapper.findPaperId(m);
                    if (!CollectionUtils.isEmpty(paperId1)) {
                        continue;
                    }
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
                            p.setId(IDUtil.generateID());
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

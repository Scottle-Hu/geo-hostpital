package com.charles.geo.service.rank.impl;

import com.charles.geo.model.Paper;
import com.charles.geo.service.rank.IPaperCatchService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 根据大学名称和疾病名称获取论文列表
 *
 * @author huqj
 * @since 1.0
 */
@Service("paperCatchService")
public class PaperCatchServiceImpl implements IPaperCatchService {

    /**
     * 日志记录器
     */
    private Logger LOGGER = Logger.getLogger(PaperCatchServiceImpl.class);

    private String url1 = "http://kns.cnki.net/kns/request/SearchHandler.ashx?action=&NaviCode=*&ua=1.21&PageName=ASP.brief_result_aspx&DbPrefix=SCDB&DbCatalog=%E4%B8%AD%E5%9B%BD%E5%AD%A6%E6%9C%AF%E6%96%87%E7%8C%AE%E7%BD%91%E7%BB%9C%E5%87%BA%E7%89%88%E6%80%BB%E5%BA%93&ConfigFile=SCDB.xml&db_opt=CJFQ%2CCJRF%2CCDFD%2CCMFD%2CCPFD%2CIPFD%2CCCND%2CCCJD&txt_1_sel=SU&txt_1_value1=(?)&txt_1_relation=%23CNKI_AND&txt_1_special1=%25&au_1_sel=AU&au_1_sel2=AF&au_1_value2=(*)&au_1_special1=%3D&au_1_special2=%25&his=0&_=";

    private String url2 = "http://kns.cnki.net/kns/brief/brief.aspx?pagename=(?)&t=(*)&keyValue=(#)&S=1";

    private String sortByRef = "&sorttype=(%E8%A2%AB%E5%BC%95%E9%A2%91%E6%AC%A1%2c%27INTEGER%27)+desc&queryid=157";

    private String prefix = "http://kns.cnki.net";

    /**
     * @param colleage 大学名称
     * @param disease  疾病名称
     * @return 论文对象列表
     */
    public List<Paper> catchPaper(String colleage, String disease) {
        System.out.println("检索的大学：" + colleage + ",检索的疾病：" + disease);
        List<Paper> paperList = new ArrayList<Paper>();
        //构造一级请求的url
        String url = getUrl1(colleage, disease);
        String content = getContent(url);
        System.out.println(content);
        //使用一级请求的返回值构造二级请求
        url = getUrl2(content, disease);
        //修改url使得结果按照被引次数排序
        String refUrl = url + sortByRef;
        System.out.println(refUrl);
        //获取具体论文结果页面源码
        String src = getContent(refUrl);
        //System.out.println(src);
        //解析需要的信息并封装到paperList
        paperList = parseSource(src);
        return paperList;
    }

    private String getUrl1(String colleage, String disease) {
        String res = url1;
        res = res.replace("(?)", disease).replace("(*)", colleage);
        return res;
    }

    private String getUrl2(String content, String disease) {
        String res = url2;
        try {
            res = res.replace("(?)", content)
                    .replace("(*)", new Date().getTime() + "")
                    .replace("(#)", URLEncoder.encode(disease, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 通过url获取内容的封装方法
     *
     * @param url
     * @return
     */
    private String getContent(String url) {
        HttpClient client = new DefaultHttpClient();
        //构造一级请求的url
        HttpGet getHttp = new HttpGet(url);
        //设置请求头信息
        getHttp.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        getHttp.setHeader("Accept-Encoding", "gzip, deflate");
        getHttp.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        getHttp.setHeader("Cookie", "UM_distinctid=162f1a262e1642-0a081781ce52c-3f3c5501-113a00-162f1a262e2717; cnkiUserKey=440e8c74-a656-44de-456f-9742badf5d06; Ecp_ClientId=5180423163202842186; RsPerPage=20; ASP.NET_SessionId=nmkk5hbdfdimkez1svvrnof3; SID_kns=123111; SID_klogin=125141; SID_krsnew=125131; Ecp_IpLoginFail=18050261.135.216.3; SID_kcms=124111; CNZZDATA3258975=cnzz_eid%3D1325162792-1524472189-%26ntime%3D1525238108; _pk_ref=%5B%22%22%2C%22%22%2C1525243479%2C%22http%3A%2F%2Fwww.cnki.net%2Fold%2F%22%5D; _pk_id=85180377-fcbe-4c46-ab88-7a87ccf259b5.1524472374.4.1525243479.1525243479.");
        getHttp.setHeader("Host", "kns.cnki.net");
        getHttp.setHeader("Referer", "http://kns.cnki.net/kns/brief/result.aspx?dbprefix=SCDB");
        getHttp.setHeader("Upgrade-Insecure-Requests", "1");
        getHttp.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        getHttp.setHeader("Upgrade-Insecure-Requests", "1");
        HttpResponse response = null;
        try {
            response = client.execute(getHttp);
        } catch (IOException e) {
            LOGGER.error("使用httpclient抓取页面内容出现问题");
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        String content = null;
        try {
            content = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //关闭资源
        client.getConnectionManager().shutdown();
        return content;
    }

    /**
     * 从源码解析需要的数据
     *
     * @param src
     * @return
     */
    private List<Paper> parseSource(String src) {
        List<Paper> paperList = new ArrayList<Paper>();
        src = src.substring(src.indexOf("<TR  bgcolor=#ffffff>"), src.indexOf("<div class='TitleLeftCell'>"));
        int index = src.indexOf("<a class=\"fz14\"");
        while (index != -1) {
            Paper paper = new Paper();
            int titleStart = src.indexOf(">", index) + 1;
            int titleEnd = src.indexOf("</a>", titleStart);
            String title = "";
            try {
                title = src.substring(titleStart, titleEnd);
                title = title.replace("<font class=Mark>", "")
                        .replace("</font>", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            paper.setTitle(title);
            System.out.println("标题：" + title);
            int authorStart = src.indexOf("<td class='author_flag'>");
            int authorEnd = src.indexOf("</td>", authorStart);
            String authors = "";
            try {
                String authorList = src.substring(src.indexOf("<a", authorStart), authorEnd);
                //处理作者列表
                authors = parseAuthor(authorList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            paper.setAuthor(authors);
            System.out.println("作者：" + authors);
            int sourceStart = src.indexOf("<a", authorEnd);
            sourceStart = src.indexOf(">", sourceStart) + 1;
            int sourceEnd = src.indexOf("</a>", sourceStart);
            String source = "";
            try {
                source = src.substring(sourceStart, sourceEnd);
            } catch (Exception e) {
                e.printStackTrace();
            }
            paper.setSource(source);
            System.out.println("来源：" + source);
            int timeStart = src.indexOf("<td", sourceEnd);
            timeStart = src.indexOf(">", timeStart) + 1;
            int timeEnd = src.indexOf("</td>", timeStart);
            String time = "";
            try {
                time = src.substring(timeStart, timeEnd).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            paper.setPublishTime(time);
            System.out.println("时间：" + time);
            int refStart = src.indexOf("<a", timeEnd);
            refStart = src.indexOf(">", refStart) + 1;
            int refEnd = src.indexOf("</a>", refStart);
            int refNum = 0;
            try {
                String ref = src.substring(refStart, refEnd);
                refNum = Integer.parseInt(ref);
            } catch (Exception e) {
                e.printStackTrace();
            }
            paper.setReference(refNum);
            System.out.println("被引：" + refNum);
            int dnumStart = src.indexOf("<span class=\"downloadCount\">") + 33;
            dnumStart = src.indexOf(">", dnumStart) + 1;
            int dnumEnd = src.indexOf("</a>", dnumStart);
            int dnum2 = 0;
            try {
                String dnum = src.substring(dnumStart, dnumEnd);
                dnum2 = Integer.parseInt(dnum);
            } catch (Exception e) {
                e.printStackTrace();
            }
            paper.setDnum(dnum2);
            System.out.println("下载：" + dnum2);
            String url = "";
            int urlStart = src.indexOf("<a target=\"online_open\"");
            if (urlStart != -1) {
                int hrefStart = src.indexOf("href='", urlStart) + 6;
                int hrefEnd = src.indexOf("'", hrefStart);
                url = prefix + src.substring(hrefStart, hrefEnd);
                src = src.substring(hrefEnd + 6);
            } else {
                src = src.substring(dnumEnd + 4);
            }
            paper.setUrl(url);
            System.out.println("url：" + url);
            System.out.println();
            index = src.indexOf("<a class=\"fz14\"");
            paperList.add(paper);
        }
        return paperList;
    }

    private String parseAuthor(String author) {
        int start = author.indexOf("<");
        int end = author.indexOf(">");
        while (start != -1 && end != -1) {
            String rem = author.substring(start, end + 1);
            author = author.replace(rem, "");
            start = author.indexOf("<");
            end = author.indexOf(">");
        }
        author = author.trim();
        return author;
    }

    public static void main(String[] args) {
        PaperCatchServiceImpl instance = new PaperCatchServiceImpl();
        instance.catchPaper("武汉大学", "白血病");
    }
}

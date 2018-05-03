package com.charles.geo.controller;

import com.charles.geo.model.InformationResponse;
import com.charles.geo.model.QueryRequest;
import com.charles.geo.service.process.IMainQueryService;
import com.charles.geo.utils.QueryRequestFactory;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author huqj
 * @description api访问入口
 * @since 1.0
 */
@Controller
@RequestMapping("/api")
public class APIController {

    /**
     * 日志记录器
     */
    private Logger LOGGER = Logger.getLogger(APIController.class);

    @Autowired
    private QueryRequestFactory queryRequestFactory;

    @Autowired
    private IMainQueryService mainQueryService;

    @RequestMapping(value = "/main", method = RequestMethod.POST)
    public void mainApi(@RequestParam("text") String text,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            QueryRequest queryRequest = queryRequestFactory.createQuery(text, null, null);
            InformationResponse informationResponse = mainQueryService.handler(queryRequest);
            //将推荐结果以json的格式返回
            JSONObject jsonObject = JSONObject.fromObject(informationResponse);
            out.write(jsonObject.toString().getBytes("utf-8"));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("调用api出现未知问题");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LOGGER.error("关闭输出流出现问题");
                }
            }
        }
    }

    //静态页面
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

}

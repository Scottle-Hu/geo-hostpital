package com.charles.geo.controller;

import com.charles.geo.model.InformationResponse;
import com.charles.geo.model.QueryRequest;
import com.charles.geo.service.process.IMainQueryService;
import com.charles.geo.utils.QueryRequestFactory;
//import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public @ResponseBody
    Object mainApi(@RequestParam("text") String text,
                   HttpServletRequest request,
                   HttpServletResponse response) {
        InformationResponse informationResponse = new InformationResponse();
        try {
            QueryRequest queryRequest = queryRequestFactory.createQuery(text, null, null);
            informationResponse = mainQueryService.handler(queryRequest);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("调用api出现未知问题");
        }
        return informationResponse;
    }

    //静态页面
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

}

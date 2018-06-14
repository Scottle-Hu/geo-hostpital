package com.charles.geo.controller;

import com.charles.geo.model.ErrorResponse;
import com.charles.geo.model.GeoPoint;
import com.charles.geo.model.InformationResponse;
import com.charles.geo.model.QueryRequest;
import com.charles.geo.service.process.IMainQueryService;
import com.charles.geo.utils.IQueryRequestFactory;
import com.charles.geo.utils.QueryRequestFactory;
//import net.sf.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.json.JsonObject;
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
    private IQueryRequestFactory queryRequestFactory;

    @Autowired
    private IMainQueryService mainQueryService;

    @RequestMapping(value = "/main", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public void mainApi(@RequestParam("text") String text,
                        @RequestParam(value = "lng", required = false) double lng,
                        @RequestParam(value = "lat", required = false) double lat,
                        @RequestParam(value = "uid", required = false) String uid,    //用户唯一id，用于个性化推荐
                        HttpServletRequest request, HttpServletResponse response) {
        OutputStream out = null;
        try {  //测试git
            out = response.getOutputStream();
            request.setCharacterEncoding("utf-8");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            ObjectMapper map = new ObjectMapper();
            String res = null;
            if (text == null || text.equals("")) {
                // 参数为空，返回错误信息
                ErrorResponse errorResponse = new ErrorResponse();
                errorResponse.setMessage("text参数不能为空");
                res = map.writeValueAsString(errorResponse);
            } else {
                long start = System.currentTimeMillis();
                QueryRequest queryRequest =
                        queryRequestFactory.createQuery(text, getIpAddress(request), new GeoPoint(lng, lat), uid);
                InformationResponse informationResponse = mainQueryService.handler(queryRequest);
                long end = System.currentTimeMillis();
                informationResponse.setResponseTime((end - start) / 1000 + "秒");
                res = map.writeValueAsString(informationResponse);
            }
            out.write(res.getBytes("utf-8"));
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("调用api出现未知问题");
            // 返回错误响应
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("推荐出错，详见异常信息");
            errorResponse.setE(e);
            ObjectMapper map = new ObjectMapper();
            String res = null;
            try {
                res = map.writeValueAsString(errorResponse);
                out.write(res.getBytes("utf-8"));
                out.flush();
                out.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }
    }

    // 静态页面
    @RequestMapping(value = "/index", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String index() {
        return "index";
    }

    /**
     * 获取客户端的ip
     *
     * @param request
     * @return
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}

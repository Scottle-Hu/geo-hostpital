package com.charles.geo.service.ip.impl;

import com.charles.geo.service.ip.IIP2RegionService;
import com.charles.geo.utils.HttpUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * @author huqj
 */
@Service("ip2regionService")
public class IP2RegionServiceImpl implements IIP2RegionService {

    private Logger LOGGER = Logger.getLogger(IP2RegionServiceImpl.class);

    /**
     * 淘宝api调用地址
     */
    private String API_ADDR = "http://ip.taobao.com/service/getIpInfo.php?ip=";

    public String getCityFromIP(String ip) {
        String url = API_ADDR + ip;
        String response = HttpUtil.getRequest(url);
        //解析结果json
        int codeStart = response.indexOf("code");
        if (codeStart != -1 && (codeStart = codeStart + 6) < response.length()) {
            int codeEnd = response.indexOf(",", codeStart);
            if (codeEnd != -1) {
                String codeStr = response.substring(codeStart, codeEnd);
                int code = -1;
                try {
                    code = Integer.parseInt(codeStr);
                } catch (Exception e) {
                    LOGGER.info("转换code为整数出错");
                }
                if (code == 0) {
                    String res = "";
                    int regionStart = response.indexOf("region");
                    int regionEnd = response.indexOf(",", regionStart);
                    if (regionStart != -1 && regionEnd != -1 && regionStart < regionEnd) {
                        res += response.substring(regionStart + 8, regionEnd);
                    }
                    int cityStart = response.indexOf("city");
                    int cityEnd = response.indexOf(",", cityStart);
                    if (cityStart != -1 && cityEnd != -1 && cityStart < cityEnd) {
                        res += response.substring(cityStart + 6, cityEnd);
                    }
                    return res;
                }
                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}

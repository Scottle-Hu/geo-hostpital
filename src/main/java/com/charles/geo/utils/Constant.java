package com.charles.geo.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 相关常量
 *
 * @author huqj
 * @since 1.0
 */
@Service("constant")
public class Constant {

    public static double DOUBLE_ZERO = 10e-6;

    public static String COUNTY = "区县";

    public static String CITY = "市";

    public static String PROVINCE = "省";

}

package com.charles.geo.utils;

/**
 * @author huqj
 * @since 1.0
 */
public class StringUtil {

    /**
     * 清理从网页上抓取的数据的格式，包括：标签、\n\t等等
     *
     * @param str
     * @return
     */
    public static String clearStringFromWeb(String str) {
        if (str == null) {
            return "";
        }
        //去除标签
        int start = str.indexOf("<");
        int end = str.indexOf(">", start);
        while (start != -1 && end != -1) {
            String rem = str.substring(start, end + 1);
            str = str.replace(rem, "");
            start = str.indexOf("<");
            end = str.indexOf(">", start);
        }
        //去除转义字符
        str = str.replace("\n", "").replace("\t", "")
                .replace("\r", "");
        //trim
        str = str.trim();
        return str;
    }

    public static void main(String[] args) {
        String string = "<p>暂时没有数据\n可以稍后再试\t你好</p>";
        System.out.println(string);
        System.out.println();
        System.out.println(clearStringFromWeb(string));
        ;
    }
}

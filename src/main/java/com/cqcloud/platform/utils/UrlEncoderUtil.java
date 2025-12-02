package com.cqcloud.platform.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * URL编码工具
 */
public class UrlEncoderUtil {

    /**
     * 对字符串进行URL编码
     */
    public static String encode(String text) {
        try {
            return URLEncoder.encode(text, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            e.printStackTrace();
            return text;
        }
    }

    /**
     * 对URL中的查询参数进行编码（保持URL结构）
     */
    public static String encodeUrlParams(String url) {
        try {
            int queryStart = url.indexOf('?');
            if (queryStart == -1) {
                return url;
            }

            String baseUrl = url.substring(0, queryStart + 1);
            String queryString = url.substring(queryStart + 1);

            String[] params = queryString.split("&");
            StringBuilder result = new StringBuilder();

            for (String param : params) {
                if (result.length() > 0) {
                    result.append("&");
                }

                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2) {
                    String encodedKey = encode(keyValue[0]);
                    String encodedValue = encode(keyValue[1]);
                    result.append(encodedKey).append("=").append(encodedValue);
                } else {
                    result.append(encode(keyValue[0]));
                }
            }

            return baseUrl + result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
    }

    /**
     * 测试中文编码
     */
    public static void main(String[] args) {
        // 测试单个字符编码
        String[] testChars = {"渝", "川", "京", "沪", "粤"};

        for (String ch : testChars) {
            String encoded = encode(ch);
            System.out.println(ch + " -> " + encoded);
        }

        // 测试完整URL
        String url = "https://tcc.tlgdiot.cn/wechat/pages/pay/index?carlicense=渝BS775W";
        String encodedUrl = encodeUrlParams(url);
        System.out.println("\n原URL: " + url);
        System.out.println("编码后: " + encodedUrl);
    }
}
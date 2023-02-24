package com.kl.nacosscan.core;

public class NacosConstant {

    public static String[] USER_AGENT_LIST = {"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/109.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/600.2.5 (KHTML, like Gecko) Version/8.0.2 Safari/600.2.5 (Applebot/0.1)",
            "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
            "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.8.131 Version/11.11",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11"};


    /**
     * nacos登录接口地址
     */
    public static String[] NACOS_LOGIN_URL = {"/v1/auth/users/login","/v1/auth/login"};
    /**
     * nacos 版本信息及验证
     */
    public static String[] NACOS_VERIFY_URLS = {"/v1/console/server/state?accessToken=&username=","/nacos/v1/console/server/state?accessToken=&username="};
    /**
     * nacos 1.x 版本 bypass 漏洞
     */
    public static String NACOS_1X_BYPASS_VERIFY_URL = "/v1/auth/users?pageNo=1&pageSize=9";

    /**
     * nacos1.x 版本 bypass漏洞 添加用户 test aaa123456
     */
    public static String NACOS_1X_BYPASS_ADDUSER_URL = "/v1/auth/users?username=anonymity&password=aaa123456";

    /**
     * 获取nacos namespace
     */
    public static String NACOS_AUTH_GET_NAMESPACE_URL = "/v1/console/namespaces";

    /**
     * 获取nacos configs
     */
    public static String NACOS_AUTH_GET_CONFIG_URL = "/v1/cs/configs";

    public static String[] NACOS_DEFAULT_PORT = {"8848"};

    public static String[] NACOS_ACTUATOR = {
            "/actuator",
            "/actuator/autoconfig",
            "/actuator/beans",
            "/actuator/env",
//        "/actuator/dump",
            "/actuator/info",
            "/actuator/trace",
//        "/actuator/heapdump",
    };


    public static String TASK_STATUS_VERIFY = "task_status_verify";
    public static String TASK_STATUS_PASS = "task_status_pass";
    public static String TASK_STATUS_WAIT = "task_status_wait";
    public static String TASK_STATUS_RUN = "task_status_run";
    public static String TASK_STATUS_STOP = "task_status_stop";

    public static String QUERY_IP_API = "http://opendata.baidu.com/api.php?query=${ip}&co=&resource_id=6006&oe=utf8";


    public static String FIND_IP_REG = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";
    public static String FIND_URL_REG = "[a-zA-z]+://[^\\s]*";
    public static String FIND_EMAIL_REG = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
    public static String FIND_PHONE_REG = "(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}";
    public static String FIND_PASS_REG = "[a-zA-Z\\w@]{5,17}";


}

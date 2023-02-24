package com.kl.nacosscan;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.log.StaticLog;
import cn.hutool.setting.yaml.YamlUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kl.nacosscan.commandparse.MainParser;
import com.kl.nacosscan.core.NacosConstant;
import com.kl.nacosscan.entity.NacosConfig;
import com.kl.nacosscan.entity.NameSpace;

import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NacosScanner {

    private static final String NAMESPACE_URL = "/v1/console/namespaces?&accessToken=${accessToken}&namespaceId=${namespaceId}";
    private static final String CONFIGS_URL = "/v1/cs/configs?dataId=&group=&appName=&config_tags=&pageNo=1&pageSize=100&tenant=${namespace}&search=accurate&accessToken=${accessToken}";
    private static final String usageEg = "eg:\n" +
            "  java -jar NacosScanner.jar -u http://127.0.0.1/nacos -at <access_token>\n" +
            "  java -jar NacosScanner.jar -u http://127.0.0.1/nacos -user <username> -pass <password>";


    private static String ROOTURL = "";
    private static String accessToken = "";
    private static String outputPath = "./result";

    public static void main(String[] args) {
        MainParser options = MainParser.parse(args);
        if(options.getOptions().isHelp()){
            System.out.println(MainParser.usage()+ "\n" +usageEg);
            return;
        }
        if(options.url.isPassedIn){
            ROOTURL = URLUtil.normalize(options.url.value);
        }
        if(options.accessToken.isPassedIn){
            accessToken = options.accessToken.value;
        }else{
            String loginParam = "username=${username}&password=${password}";
            if(options.username.isPassedIn && options.password.isPassedIn){
              String accessTokenVal = getAccessToken(loginParam.replace("${username}",options.username.value).replace("${password}",options.password.value));
              if(ObjectUtil.isNotNull(accessTokenVal)){
                  accessToken = accessTokenVal;
              }else{
                  System.out.println("access_token 获取失败");
                  System.out.println(usageEg);
                  return;
              }
            }
        }
        startProcess();
    }


    public static String getAccessToken(String param){
        for(String url:NacosConstant.NACOS_LOGIN_URL){
            HttpResponse httpResponse = HttpRequest.post(ROOTURL+url).body(param).execute();
            if(httpResponse.getStatus() == 200){
                JSONObject jsonObject = JSONObject.parseObject(httpResponse.body());
                String accessToken = jsonObject.getString("accessToken");
                if(ObjectUtil.isNotNull(accessToken)){
                    return accessToken;
                }
            }
        }
        return null;
    }

    public static void startProcess(){

        HttpResponse response = HttpRequest.get(ROOTURL+NAMESPACE_URL.replace("${accessToken}",accessToken).replace("${namespaceId}",""))
                .header(Header.USER_AGENT, NacosConstant.USER_AGENT_LIST[RandomUtil.randomInt(0,10)])
                .execute();

        JSONArray nameSpaces = JSONObject.parseObject(response.body()).getJSONArray("data");
        for(Object json : nameSpaces){
            JSONObject item = (JSONObject) json;
            NameSpace nameSpace = new NameSpace();
            nameSpace.setNamespace(item.getString("namespace"));
            nameSpace.setNamespaceShowName(item.getString("namespaceShowName"));
            nameSpace.setConfigCount(item.getString("configCount"));
            nameSpace.setType(item.getString("type"));
            findNameSpaceConfig(nameSpace);
        }

        outputResult();
    }



    public static void findNameSpaceConfig(NameSpace nameSpace){
        HttpResponse response = HttpRequest.get(ROOTURL+CONFIGS_URL.replace("${accessToken}",accessToken).replace("${namespace}",nameSpace.getNamespace()))
                .header(Header.USER_AGENT, NacosConstant.USER_AGENT_LIST[RandomUtil.randomInt(0,10)])
                .execute();
        JSONArray configs = JSONObject.parseObject(response.body()).getJSONArray("pageItems");
        for(Object obj : configs){
            JSONObject config = (JSONObject) obj;
            NacosConfig nacosConfig = new NacosConfig();
            nacosConfig.setId(config.getString("id"));
            nacosConfig.setDataId(config.getString("dataId"));
            nacosConfig.setContent(config.getString("content"));
            nacosConfig.setGroup(config.getString("group"));
            nacosConfig.setType(config.getString("type") == null ? "" : config.getString("type"));
            if(nacosConfig.getType().equals("yaml")){
                parseYAML(nacosConfig);
            }
            findLeakInfo(nacosConfig);
        }
    }


    private static Set<String> findIps = new HashSet<>();
    private static Set<String> findURLs = new HashSet<>();
    private static Set<String> findEmails = new HashSet<>();
    private static Set<String> findPhones = new HashSet<>();
    private static Set<String> findPasswords = new HashSet<>();


    public static void findLeakInfo(NacosConfig nacosConfig){
        String content = nacosConfig.getContent();
        Pattern pattern = Pattern.compile(NacosConstant.FIND_IP_REG);
        Matcher matcher = pattern.matcher(content);
        while(matcher.find()){
            String findIp = content.substring(matcher.start(),matcher.end());
            findIps.add(findIp);
        }

        pattern = Pattern.compile(NacosConstant.FIND_URL_REG);
        matcher = pattern.matcher(content);
        while(matcher.find()){
            String findStr = content.substring(matcher.start(),matcher.end());
            findURLs.add(findStr.replaceAll("\"","").replaceAll(",","").trim());
        }

        pattern = Pattern.compile(NacosConstant.FIND_EMAIL_REG);
        matcher = pattern.matcher(content);
        while(matcher.find()){
            String findStr = content.substring(matcher.start(),matcher.end());
            findEmails.add(findStr.trim());
        }

        pattern = Pattern.compile(NacosConstant.FIND_PHONE_REG);
        matcher = pattern.matcher(content);
        while(matcher.find()){
            String findStr = content.substring(matcher.start(),matcher.end());
            findPhones.add(findStr.trim());
        }

        pattern = Pattern.compile(NacosConstant.FIND_PASS_REG);
        matcher = pattern.matcher(content);
        while(matcher.find()){
            String findStr = content.substring(matcher.start(),matcher.end());
            findPasswords.add(findStr.trim());
        }
    }


    private static List<Dict> jasyptDicts = new ArrayList<>();
    private static List<Dict> springDicts = new ArrayList<>();
    private static List<Dict> minioDicts = new ArrayList<>();
    private static List<Dict> wechatDicts = new ArrayList<>();
    private static List<Dict> aliyunDicts = new ArrayList<>();
    private static List<Dict> ftpDicts = new ArrayList<>();

    public static void parseYAML(NacosConfig nacosConfig){
        StringReader reader =new StringReader(nacosConfig.getContent());
        Dict dict = YamlUtil.load(reader);
        Dict jasyptDict = dict.filter("jasypt");
        Dict springDict = dict.filter("spring");
        Dict minioDict = dict.filter("minio");
        Dict wechatDict = dict.filter("wechat","weChat");
        Dict aliyunDict = dict.filter("aliyun");
        Dict ftpDict = dict.filter("ftp");

        if(jasyptDict.size() > 0) jasyptDicts.add(jasyptDict);
        if(springDict.size() > 0) springDicts.add(springDict);
        if(minioDict.size() > 0) minioDicts.add(minioDict);
        if(wechatDict.size() > 0) wechatDicts.add(wechatDict);
        if(aliyunDict.size() > 0) aliyunDicts.add(aliyunDict);
        if(ftpDict.size() > 0) ftpDicts.add(ftpDict);
    }


    public static void outputResult(){
        if(jasyptDicts.size() > 0){
            for(Dict dict: jasyptDicts){
                findDictDeep(dict,"jasypt");
            }
        }
        if(springDicts.size() > 0){
            List<Dict> redisDicts = new ArrayList<>();
            List<Dict> datasourceDicts = new ArrayList<>();
            List<Dict> rabbitmqDicts = new ArrayList<>();
            List<Dict> mailDicts = new ArrayList<>();
            List<Dict> mongodbDicts = new ArrayList<>();
            List<Dict> ldapDicts = new ArrayList<>();

            for(Dict dict: springDicts){
                Dict springDict = Dict.parse(dict.get("spring"));
                Dict redisDict = springDict.filter("redis");
                Dict datasourceDict = springDict.filter("datasource","dataSource");
                Dict rabbitmqDict = springDict.filter("rabbitmq","rabbitMq");
                Dict mailDict = springDict.filter("mail","email");
                Dict dataDict = springDict.filter("data");
                if(dataDict.size() > 0)  {
                    Dict mongodbDict = Dict.parse(dataDict.get("data")).filter("mongodb");
                    if (mongodbDict.size() > 0) mongodbDicts.add(mongodbDict);
                }
                Dict ldapDict = springDict.filter("ldap");

                if (redisDict.size() > 0) redisDicts.add(redisDict);
                if (datasourceDict.size() > 0) datasourceDicts.add(datasourceDict);
                if (rabbitmqDict.size() > 0) rabbitmqDicts.add(rabbitmqDict);
                if (mailDict.size() > 0) mailDicts.add(mailDict);
                if (ldapDict.size() > 0) ldapDicts.add(ldapDict);
            }

            for(Dict dict: redisDicts){
                System.out.println("------------------------");
                findDictDeep(dict,"redis");
            }

            for(Dict dict: datasourceDicts){
                System.out.println("------------------------");
                findDictDeep(dict,"datasource");
            }

            for(Dict dict: rabbitmqDicts){
                System.out.println("------------------------");
                findDictDeep(dict,"rabbitmq");
            }

            for(Dict dict: mailDicts){
                System.out.println("------------------------");
                findDictDeep(dict,"mail");
            }

            for(Dict dict: ldapDicts){
                System.out.println("------------------------");
                findDictDeep(dict,"ldap");
            }

        }
        if(minioDicts.size() > 0){
            for(Dict dict: minioDicts){
                System.out.println("------------------------");
                findDictDeep(dict,"minio");
            }
        }
        if(wechatDicts.size() > 0){
            for(Dict dict: wechatDicts){
                System.out.println("------------------------");
                findDictDeep(dict,"wechat");
            }
        }
        if(aliyunDicts.size() > 0){
            for(Dict dict: aliyunDicts){
                System.out.println("------------------------");
                findDictDeep(dict,"aliyun");
            }
        }
        if(ftpDicts.size() > 0){
            for(Dict dict: ftpDicts){
                System.out.println("------------------------");
                findDictDeep(dict,"ftp");
            }
        }
        if(findIps.size() > 0){
            System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓【ip】↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
            for(String ip: findIps){
                String result = HttpUtil.get(NacosConstant.QUERY_IP_API.replace("${ip}",ip));
                JSONObject resultJson = JSONObject.parseObject(result);
                JSONArray data = resultJson.getJSONArray("data");
                if(data.size() > 0){
                    System.out.println(ip + " (" + data.getJSONObject(0).getString("location") + ")");
                }else{
                    System.out.println(ip + " (未知归属地)");
                }
            }
        }
        if(findURLs.size() > 0){
            System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓【url】↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
            for(String url: findURLs){
                System.out.println(url);
            }
        }
        if(findEmails.size() > 0){
            System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓【email】↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
            for(String email: findEmails){
                System.out.println(email);
            }
        }
        if(findPhones.size() > 0){
            System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓【phone】↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
            for(String phone: findPhones){
                System.out.println(phone);
            }
        }
        if(findPasswords.size() > 0){
            System.out.println("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓【疑似密码】↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
            for(String phone: findPasswords){
                System.out.println(phone);
            }
        }
    }

    private static String redKeys = "username,user,password,pass,host,ip,url,appid,secret,access-key,secret-key,port,wxappid,appsecret";


    private static void findDictDeep(Dict dict,String logType) {
        Set<Map.Entry<String, Object>> entries = dict.entrySet();
        for (Map.Entry entry : entries) {
            Dict subDict = Dict.parse(dict.get(entry.getKey()) == null ? new Object() : dict.get(entry.getKey()));
            if(subDict.size() > 0){
                findDictDeep(subDict,logType);
            } else {
                if(redKeys.indexOf(entry.getKey().toString()) != -1){
                    StaticLog.info("\u001B[32m【{}】\u001B[0m -- \u001B[31m {} : {} \u001B[0m", logType, entry.getKey(), entry.getValue());
                }else{
                    StaticLog.info("\u001B[32m【{}】\u001B[0m -- {} : {}", logType, entry.getKey(), entry.getValue());
                }
            }
        }
    }

}

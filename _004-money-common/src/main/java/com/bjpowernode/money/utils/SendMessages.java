package com.bjpowernode.money.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class SendMessages {
    //集合用来装发短信需要的参数
    private static Map<String,String> map = new HashMap<>();

    public static void SendMessages(String phone,String content){
        //请求地址 url
        String url = "https://way.jd.com/chuangxin/dxjk";
        //申请的appkey
        map.put("appkey","01a16cb8a1a18177c87867a2a07ba558");
        //需要发送的手机号码
        map.put("mobile",phone);
        //发送内容
        map.put("content","【创信】"+content);
        //模拟报文
        String result = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 0,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"ReturnStatus\": \"Success\",\n" +
                "        \"Message\": \"ok\",\n" +
                "        \"RemainPoint\": 1052849,\n" +
                "        \"TaskID\": 69373036,\n" +
                "        \"SuccessCounts\": 1\n" +
                "    },\n" +
                "    \"requestId\": \"7777cc0f5afe4cda957146c7e747229f\"\n" +
                "}";

      /*  //发送短信
        try {
            *//*result = HttpClientUtil.doGet(url,map);*//*
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
       return "success";*/
    }
}

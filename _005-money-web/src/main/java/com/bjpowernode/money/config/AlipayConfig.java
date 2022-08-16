package com.bjpowernode.money.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2021000121644526";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCXi3zvXyv0vU9jm337BwcTpOxhN4IvBzhD1vDKnXxYuON5Ko1iqKnJzeBmsESXAUbDLnyompIPtiuoi1deAgAHX9FkwH8mWgUtg7EtUvWxaBx++o8qCaydjEXD8h5fShvEgdS1+PY6uxlO1qRcv5v5CV9u6hn3QZcav0s5vIlUTKYqCoIuNjdf0K5y5UjmggqgUO+scb+T4G9AUtNvItgs6G2mcuDRloiyS2n8iXFzntsDN5g6ESIYwM6d4KCrf+lXLO0YqscvJLL8BSGCNEON86ZNRLSA9OJst73HwIaLX3NPxiNNiIACIlbgOpS4DPpcf/AgMcb0ZIDtJN6D+4wxAgMBAAECggEAafKRI+DLoE0BxcedqfRd1DQHNmhSUYKgqX0J1v2HQ5fvIHTJZ+I24elXKzOsoE26JRbI/C8kUmuv1iOkrwqC7qTkhaW2vx9MY2egZOmM77yzv2p40JD25yZ8V+NbrI7pHtcN2TL3IlRP+zhArL8g0ljlKWvtP1nRxFdcxj6zXAUuQrQcGznnOVO2qheOeeni5MCQIXPUo0Upg5ukKPX70eDErAU3ENx2KheEex/dkfHQkHusQP/ibXN0DZD4a6PlXNMsfe9puxcTjTQGuxo0r51ylUK3Se2xy6NVksOJ/PKeNpltw7u7cpEtujTAqpsb2Z4sKXVVVruvAcHIYhB4AQKBgQDTg/PZkjm73PEAxGV9nyDJYle5H145B+CBP8mp19i7a6VCkvVAWXn9wRkMBUhtv7GgqqABSMQv1xr44fZEahd8k6f/m8Iti/2uOFDyRnhwH/jiyKaFrlrNLjICEcBnmnjHIH9V3Lau+oE/2fKOf6raRxBUe17IAsqkgQDESmcK8QKBgQC3arRRNHbagAH+7v9+qEA6sK8tFghwl/3B5xLQJU6qzs2B4FsuNT67P0DhNV80WN4vGkrLF0Vwrc7Qqo423JmCbWeH8A19UWTpUWaWjxo93n8PLXiClY9601Qwc7HwVwuNuhnpcvTujTJCFXnnLj/IeG6afrL53r5hmAT68qwVQQKBgG5uwV1CPER6iZX19GtPLsYWQ2jTGNW76BpzXG4B1RWdkU4d/lBXpxq2u8YL6BHs+OfCzgF9pXi43Us+Zt63aYXQbfFFuG8hXRSeMJ0d7/nSp5Z4XoEDVEM2MEPmUseE3jRRIkqJDxRylBLrTvnZbDHE0qDZPNY+puCWvfi+Q+hxAoGAINWFOhQ+QpcvxEdcjXvpI5iFg3iyQhWzY4bOI1zKXGFxAJL9P6Rgeny0TJyVUnWmMKBwSzL7R1SV9VzS4JdVE7gqFHewsoJjojDkQIeVlwXBkSH+tMG5Ua+LHrFXZULv2os6IERn/FxiZtoZXH03BdgpVJbhy0FIq4ffPF082QECgYARjA8uWqNS3d60T6bfl0jlgt/RQ9HcRaSHw2xqS4/Y2VoHae0LYR5Afq/ZSb8JF/cF1yE9rn+fLSeXEjVOayMcGrZkZtPvD4MQaQUmRQKZ6lpmaK9PHoMbWDYUrlbc9R+xhq0cAV+vpf1K9LGIONdm8gQhHWinzNIyY/JYBZ8h1g==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAl/j7ygf7sXOXH8RLhsb/6ns4X8hlsjj5IP0lt7iDwyvVX32hzc1KdTVNRDwQCDgc+ObRK3PIflh/9I5aUKjJnuKYV/nEdmyswHfNRZFevLO/YOtbh1mtSwBlqTNFqHlHgLDipKu2raTwqBEfPu9cW0OePPcIyUG+5ZvjR8VL5CFDpLhhYBmGV+KGtQZ3Si4iLFddPlDC0vEMsp/GrGSBx37LxPOxPU/MiddYgNavlDwoDRrTOoLrRd94K3o1DqE5xXBByivtL0YoTgm3nosUunX4Aw4rw0PW4BqORcZ+7/YO5vmq/5FVQ94jrCODnQPLCOI7KDhfg3Fpz509mB6IxwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/101-aliPay-SDK/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:9005/_005-money-web/loan/page/payBack";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


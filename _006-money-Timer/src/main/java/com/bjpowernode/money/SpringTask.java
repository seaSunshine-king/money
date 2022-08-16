package com.bjpowernode.money;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.money.utils.HttpClientUtil;
import com.bjpowernode.money.utils.SendMessages;
import entity.RechargeRecord;
import entity.User;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import service.IncomeService;
import service.RechargeService;
import service.UserService;

import java.util.Date;
import java.util.List;

@Component
public class SpringTask {

    @Reference(interfaceClass = IncomeService.class,timeout = 20000,version = "1.0.0")
    IncomeService incomeService;

    @Reference(interfaceClass = RechargeService.class,timeout = 20000,version = "1.0.0")
    RechargeService rechargeService;

    @Reference(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0")
    UserService userService;

    //生成收益计划
    @Scheduled(cron = "0/5 * * * * ?")
    public void generatePlan(){
        System.out.println("-------begin------");
        incomeService.generatePlan();
        System.out.println("---------end---------");
    }

    //收益返现
    @Scheduled(cron = "0/30 * * * * ?")
    public void cashBack(){
        System.out.println("---------cashBackBegin----------");
        incomeService.cashBack();
        System.out.println("--------cashBackEnd------");
    }


    //订单处理
    //支付失败：修改订单状态为2
    //课后：发个短信 支付失败
    //支付成功：修改订单状态为1  更新余额
    //课后：发短信 充值成功
    @Scheduled(cron = "* 0/5 * * * ?")
    public void doRechargeRecord(){
        System.out.println("---------doRechargeRecordBegin-----------");
        //查询状态为0 订单==》List
        List<RechargeRecord> rechargeRecordList = rechargeService.queryRechargeRecordByZero();
        System.out.println(rechargeRecordList);
        if(rechargeRecordList==null || rechargeRecordList.size()==0){
            return;
        }
        //遍历集合，每个订单 询问 支付宝 交易状态
        for(RechargeRecord rechargeRecord:rechargeRecordList){
            String result = null;
            try {
                result = HttpClientUtil.doGet("http://localhost:9007/_007-money-pay/loan/page/queryOrder?out_trade_no="+rechargeRecord.getRechargeNo());
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(result);
            //解析返回值
            //拿到状态码，判断
            JSONObject jsonObject = JSONObject.parseObject(result).getJSONObject("alipay_trade_query_response");
            String code = jsonObject.getString("code");
            System.out.println(code);
            //我们定时器里只对支付成功的订单进行处理，不等于10000的订单会一直查询，直到成功为止
            if(StringUtils.equals(code,"10000")&&code!=null){
                //再判断交易状态
                String trade_status = jsonObject.getString("trade_status");

                User user = userService.queryUserInfoById(rechargeRecord.getUid());
                //交易状态信息不是成功的情况
                if(StringUtils.equals(trade_status,"WAIT_BUYER_PAY")){
                    //<10分钟，发短信提醒用户尽快完成支付,超过五分钟不管，一直等到超过两天，订单交易失败
                    long time = rechargeRecord.getRechargeTime().getTime();
                    long time1 = new Date().getTime();
                    if(time1-time<=600000){
                        //根据用户id查询用户电话
                        String content = "您有一笔充值订单等待支付，请在10分钟内完成支付，超时订单将自动作废，如有疑问请联系客服：400-888-9999";
                        SendMessages.SendMessages(user.getPhone(),content);
                    }
                    //超过一天不支付，订单作废，交易失败 ，状态该为2
                    if(time1-time>86400000){
                        int num = rechargeService.modifyRechargeStatusFail(rechargeRecord.getRechargeNo());
                    }
                }
                //交易关闭 //支付失败：修改订单状态为2
                if(StringUtils.equals(trade_status,"TRADE_CLOSED")){
                    //交易失败，将订单状态改为2
                    rechargeService.rechargeFail(rechargeRecord.getRechargeNo());
                    //发短信，反馈用户
                    String content = "您有一笔充值订单支付失败，订单已取消，如是本人操作请忽略，如有疑问请联系客服：400-888-9999";
                    SendMessages.SendMessages(user.getPhone(),content);
                }
                //交易成功
                if(StringUtils.equals(trade_status,"TRADE_SUCCESS")) {

                    //订单完好，将状态修改为1 ，将充值金额加进用户账户
                    boolean b = rechargeService.rechargeSuccess(rechargeRecord);
                    if (!b) {
                        //给用户反馈，打消用户疑虑
                        String content = "您的充值订单已成功支付，预计在24h内到账，请注意查收，如有疑问请联系客服：400-888-9999";
                        SendMessages.SendMessages(user.getPhone(), content);
                    }
                    //已到账，反馈用户
                    String content = "您的充值金额已到账，请注意查收，如有疑问请联系客服：400-888-9999";
                    SendMessages.SendMessages(user.getPhone(), content);

                }
            }
        }
        System.out.println("---------doRechargeRecordEnd-----------");
    }
}

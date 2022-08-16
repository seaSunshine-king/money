package com.bjpowernode.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import entity.LoanInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import service.BidInfoService;
import service.LoanInfoService;
import service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Controller
public class IndexController {

    //在dubbo springboot 使用时，在需要调用的服务接口上使用 @Reference 即可直接调用远程服务
    @Reference(interfaceClass = LoanInfoService.class,timeout = 20000,version = "1.0.0")
    LoanInfoService loanInfoService;

    @Reference(interfaceClass =UserService.class,timeout = 20000,version = "1.0.0")
    UserService userService;

    @Reference(interfaceClass =BidInfoService.class,timeout = 20000,version = "1.0.0")
    BidInfoService bidInfoService;

    @GetMapping("index")
    public String index(Model model){

        //线程池个数，一般建议是CPU内核数 或者 CPU内核数据*2
       /* ExecutorService executorService = Executors.newFixedThreadPool(8);

        for (int i = 0; i < 1000; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    loanInfoService.queryLoanInfoHistoryRateAvg();
                }
                });
        }*/

        //动力金融网历史年化收益率  （产品平均利率）
        Double loanInfoHistoryRateAvg = loanInfoService.queryLoanInfoHistoryRateAvg();
        model.addAttribute("loanInfoHistoryRateAvg",loanInfoHistoryRateAvg);

        //平台用户数 （注册总人数）
        Long userCount = userService.queryUserCount();
        model.addAttribute("userCount",userCount);

        //累计成交额  （投资总金额）
        Double bidMoneySum = bidInfoService.queryBidMoneySum();
        model.addAttribute("bidMoneySum",bidMoneySum);

        Map<String,Object> parasMap = new HashMap<>();

        //新手宝
        parasMap.put("ptype",0);
        parasMap.put("start",0);
        parasMap.put("content",1);
        List<LoanInfo> loanInfoList_X=loanInfoService.queryLoanInfosByTypeAndNum(parasMap);
        model.addAttribute("loanInfoList_X",loanInfoList_X);

        //优选标
        parasMap.put("ptype",1);
        parasMap.put("start",0);
        parasMap.put("content",4);
        List<LoanInfo> loanInfoList_Y=loanInfoService.queryLoanInfosByTypeAndNum(parasMap);
        model.addAttribute("loanInfoList_Y",loanInfoList_Y);

        //散标
        parasMap.put("ptype",2);
        parasMap.put("start",0);
        parasMap.put("content",8);
        List<LoanInfo> loanInfoList_S=loanInfoService.queryLoanInfosByTypeAndNum(parasMap);
        model.addAttribute("loanInfoList_S",loanInfoList_S);
        return "index";
    }

}

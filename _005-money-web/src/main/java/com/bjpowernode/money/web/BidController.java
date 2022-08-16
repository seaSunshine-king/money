package com.bjpowernode.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.money.utils.*;
import entity.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import service.BidInfoService;
import service.IncomeService;
import service.LoanInfoService;
import service.RedisService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

@Controller
public class BidController {

    @Reference(interfaceClass = BidInfoService.class,timeout = 20000,version = "1.0.0")
    BidInfoService bidInfoService;

    @Reference(interfaceClass = LoanInfoService.class,timeout = 20000,version = "1.0.0")
    LoanInfoService loanInfoService;

    @Reference(interfaceClass = RedisService.class,timeout = 20000,version = "1.0.0")
    RedisService redisService;

    @Reference(interfaceClass = IncomeService.class,timeout = 20000,version = "1.0.0")
    IncomeService incomeService;

    /**
     * 投资
     * @param request
     * @param bidMoney 投资金额
     * @param loanId 投资产品编号
     * @return
     */
    @PostMapping("loan/page/invest")
    @ResponseBody
    public Object invest (HttpServletRequest request,
                         @RequestParam(name = "bidMoney",required = true)Double bidMoney,
                         @RequestParam(name="loanId",required = true)Integer loanId)
    {
        //验证登录
        User user =(User) request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)) {
            return Result.error("请先完成登录,再投资");
        }
        //实名认证验证
        System.out.println(user.getName());
        System.out.println(user.getIdCard());
        if(!ObjectUtils.allNotNull(user.getName(),user.getIdCard())){
            return Result.error("请先完成实名认证,再投资");
        }
        //基础数据验证
        //非空
        if(bidMoney==null){
            return  Result.error("金额不能为空");
        }
        //非0
        if(bidMoney==0){
            return Result.error("金额不能为0");
        }
        //数字，正整数
        String s = bidMoney.toString();
        Pattern pattern = compile("[0-9]*");
        if(pattern.matcher(s).matches()){
            System.out.println("不是正整数");
            return Result.error("金额必须为正整数");
        }
        //100的倍数
        if(bidMoney%100>0){
            return Result.error("金额必须为100的倍数");
        }
        //业务验证
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(loanId);
        //金额是否在最小投资金额和最大投资金额之间
        if(loanInfo.getBidMinLimit()>bidMoney || loanInfo.getBidMaxLimit()<bidMoney){
            return Result.error("金额应在"+loanInfo.getBidMinLimit()+"~"+loanInfo.getBidMaxLimit()+"范围内");
        }
        //是否在剩余可投金额范围内
        if(bidMoney>loanInfo.getLeftProductMoney()){
            return Result.error("金不不能超过剩余可投金额:"+loanInfo.getLeftProductMoney());
        }

        //投资
        Map<String,Object> map = new HashMap();
        map.put("userId",user.getId());
        map.put("loanId",loanId);
        map.put("bidMoney",bidMoney);
        map.put("phone",user.getPhone());

        //维护线程池
        /*ExecutorService instance = ThreadPool.getInstance();*/

        Integer result =  bidInfoService.invest(map);;
        /*instance.submit(new Runnable() {
            @Override
            public void run() {
                Integer result = bidInfoService.invest(map);
            }

        });*/

        if(result== 20000){
            List<BidInfo> bidTops = redisService.zpop();
            int i = 0;
            Double money = 0.0;
            for(BidInfo bidInfo:bidTops){
                if(bidInfo.getPhone()==user.getPhone()){
                    i=1;
                    money = bidInfo.getBidMoney();
                }
            }
            if(!ObjectUtils.allNotNull(bidTops)||bidTops.size()==0||i==0){
                redisService.zpush(user.getPhone(),bidMoney.intValue());
            }else {
                redisService.zpush(user.getPhone(),(money.intValue()+bidMoney.intValue()));
            }
            return Result.success(result);
        }
        return Result.error(result);
    }

    /*class InvestThread extends Thread{
        private Result result = null;
        @Override
        public void run() {

        }
    }*/


    /**
     * 查看全部收益页面，带分页
     * @return
     */
    @GetMapping("loan/myIncome")
    public String myIncome(HttpServletRequest request, Model model){
        User user = (User) request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myIncome";
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        PageModel<IncomeRecord> page = new PageModel<>();

        //封装当前页数
        page.setCurrentPage(1);
        //每页显示记录条数
        page.setPageSize(6);
        //封装用户id
        page.setUserId(user.getId());

        //查询总记录数
        Integer totalCount = incomeService.queryIncomeRecordCountByUserId(user.getId());
        //总页数
        double tc = totalCount.doubleValue();
        Double num = Math.ceil(tc / 6);//向上取整,除不尽的时候都向上取整
        Integer totalPage = Integer.valueOf(num.intValue());
        page = incomeService.queryIncomeRecordByPage(page);
        page.setTotalSize(totalCount);
        page.setTotalPage(totalPage);
        request.getSession().setAttribute("pageInfo",page);
        model.addAttribute("pageInfo",page);
        return "myIncome";
    }


    /**
     * 用户全部收益记录带分页--首页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myIncome/firstPage")
    public String firstPage(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myIncome";
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        PageModel<IncomeRecord> pageInfo = (PageModel<IncomeRecord>)request.getSession().getAttribute("pageInfo");
        pageInfo.setCurrentPage(1);
        pageInfo = incomeService.queryIncomeRecordByPage(pageInfo);
        request.getSession().setAttribute("pageInfo",pageInfo);
        model.addAttribute("pageInfo",pageInfo);
        return "myIncome";
    }


    /**
     * 用户全部收益记录带分页--上一页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myIncome/upPage")
    public String upPage(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myIncome";
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        PageModel<IncomeRecord> pageInfo =(PageModel) request.getSession().getAttribute("pageInfo");
        if(pageInfo.getCurrentPage()>1){
            pageInfo.setCurrentPage(pageInfo.getCurrentPage()-1);
        }
        pageInfo = incomeService.queryIncomeRecordByPage(pageInfo);
        request.getSession().setAttribute("pageInfo",pageInfo);
        model.addAttribute("pageInfo",pageInfo);
        return "myIncome";
    }


    /**
     * 用户全部充值记录带分页--下一页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myIncome/nextPage")
    public String nextPage(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myIncome";
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        PageModel<IncomeRecord> pageInfo =(PageModel) request.getSession().getAttribute("pageInfo");
        System.out.println(pageInfo);
        if(pageInfo.getCurrentPage()<pageInfo.getTotalPage()){
            pageInfo.setCurrentPage(pageInfo.getCurrentPage()+1);
        }
        pageInfo = incomeService.queryIncomeRecordByPage(pageInfo);
        System.out.println(pageInfo);
        request.getSession().setAttribute("pageInfo",pageInfo);
        model.addAttribute("pageInfo",pageInfo);
        return "myIncome";
    }


    /**
     * 用户全部充值记录带分页--尾页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myIncome/lastPage")
    public String lastPage(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myIncome";
            return "redirect:/loan/page/login?Return="+ReturnUrl;
        }
        PageModel<IncomeRecord> pageInfo = (PageModel)request.getSession().getAttribute("pageInfo");
        pageInfo.setCurrentPage(pageInfo.getTotalPage());
        pageInfo = incomeService.queryIncomeRecordByPage(pageInfo);
        request.getSession().setAttribute("pageInfo",pageInfo);
        model.addAttribute("pageInfo",pageInfo);
        return "myIncome";
    }
}

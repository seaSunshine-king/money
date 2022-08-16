package com.bjpowernode.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.money.utils.Result;
import entity.*;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import service.BidInfoService;
import service.FinanceService;
import service.IncomeService;
import service.RechargeService;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;


@Controller
public class FinanceController {

    @Reference(interfaceClass = FinanceService.class,timeout = 20000,version = "1.0.0")
    FinanceService financeService;

    @Reference(interfaceClass = BidInfoService.class,timeout = 20000,version = "1.0.0")
    BidInfoService bidInfoService;

    @Reference(interfaceClass = RechargeService.class,timeout = 20000,version = "1.0.0")
    RechargeService rechargeService;

    @Reference(interfaceClass = IncomeService.class,timeout = 20000,version = "1.0.0")
    IncomeService incomeService;

    /**
     * 查询用户账户余额
     * @param request
     * @return
     */
    @GetMapping("loan/page/balance")
    @ResponseBody
    public Object balance(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            return Result.error("请登陆后再查看余额");
        }
        FinanceAccount financeAccount = financeService.queryFinanceAccountByUserId(user.getId());
        if(!ObjectUtils.allNotNull(financeAccount)){
            return Result.error("系统繁忙,请稍后查询,如有疑问请拨打400-699-2121");
        }
        return Result.success(financeAccount.getAvailableMoney()+"");
    }

    /**
     * 去我的小金库
     * 最近投资
     * 最近充值
     * 最近收益
     * @return
     */
    @GetMapping("loan/myCenter")
    public String myCenter(HttpServletRequest request, Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            model.addAttribute("balance","请登陆后再查看余额");
            return "redirect:/index";
        }else {
            FinanceAccount financeAccount = financeService.queryFinanceAccountByUserId(user.getId());
            if(!ObjectUtils.allNotNull(financeAccount)){
               model.addAttribute("balance","系统繁忙,请稍后查询,如有疑问请拨打400-699-2121");
            }else {
                model.addAttribute("balance",financeAccount.getAvailableMoney());
            }
            //最近投资
            List<BidInfo> bidList =  bidInfoService.queryBidInfoByUserId(user.getId());
            if(ObjectUtils.allNotNull(bidList) && bidList.size()!=0) {
                /* List<BidInfo> bidInfoList = null;*/
                if (bidList.size() > 5) {
                    bidList = bidList.subList(0, 5);
                }
                bidList = bidList.subList(0, bidList.size());
                bidList.sort(new Comparator<BidInfo>() {
                    @Override
                    public int compare(BidInfo o1, BidInfo o2) {
                        return o2.getBidTime().compareTo(o1.getBidTime());
                    }
                });
                model.addAttribute("bidList", bidList);
            }
            //最近充值
            List<RechargeRecord> rechargeList = rechargeService.queryRecentRechargeRecordByUserId(user.getId());
                model.addAttribute("rechargeList",rechargeList);
            //最近收益
            List<IncomeRecord> incomeList = incomeService.queryRecentIncomeRecordByUserId(user.getId());
            model.addAttribute("incomeList",incomeList);
        }
        model.addAttribute("imgUrl",user.getHeaderImage());
        return "myCenter";
    }


}

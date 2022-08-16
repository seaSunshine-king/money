package com.bjpowernode.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.money.utils.PageModel;
import com.bjpowernode.money.utils.Result;
import entity.BidInfo;
import entity.FinanceAccount;
import entity.LoanInfo;
import entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/loan")
public class LoanController {

    @Reference(interfaceClass = FinanceService.class,timeout = 20000,version = "1.0.0")
    FinanceService financeService;

    @Reference(interfaceClass = BidInfoService.class,timeout = 20000,version = "1.0.0")
    BidInfoService bidInfoService;

    @Reference(interfaceClass = LoanInfoService.class,timeout = 20000,version = "1.0.0")
    LoanInfoService loanInfoService;

    @Reference(interfaceClass = RedisService.class,timeout = 20000,version = "1.0.0")
    RedisService redisService;

    /**
     * 分页、投资排行
     * @param ptype
     * @param currentPage
     * @param model
     * @param request
     * @return
     */
    @GetMapping("/loan")
    public String loanInfo(@RequestParam(name="ptype",required = true)Integer ptype, @RequestParam(value="currentPage",defaultValue="1",required=false)Integer currentPage, Model model, HttpServletRequest request){
        //物理分页
        try {
            //后台判断页码合理性
            if(currentPage<1){
                currentPage=1;
            }
            //总记录数
            Integer totalCount = loanInfoService.queryLoanInfoTotalCount(ptype);
            //总页数
            double tc = totalCount.doubleValue();
            Double num = Math.ceil(tc / 9);//向上取整,除不尽的时候都向上取整
            Integer totalPage = Integer.valueOf(num.intValue());
            //后台判断页码合理性
            if(currentPage>totalPage){
                currentPage=totalPage;
            }

            PageModel<LoanInfo> page = loanInfoService.queryLoanInfosByTypeAndPage(currentPage, ptype);
            //查询缓存中资记录并排序，展现到·页面
           /* List<BidInfo> list = bidInfoService.queryBidInfoBySort(ptype);*/
            //排序
            /*Collections.sort(list, new Comparator<BidInfo>() {
                @Override
                public int compare(BidInfo o1, BidInfo o2) {
                    return (int) (o2.getBidMoney()-o1.getBidMoney());
                }
            });
            List<BidInfo> bidInfoList = list.subList(0, 5);
            int id = 0;
            for(BidInfo bidInfo:bidInfoList){
                id++;
                bidInfo.setId(id);
            }*/
            List<BidInfo> bidInfoList = redisService.zpop();

            //封装总记录数
            page.setTotalSize(totalCount);
            //封装总页数
            page.setTotalPage(totalPage);
            //封装产品类型
            page.setPtype(ptype);
            /*model.addAttribute("loanInfoList", page.getList());//显示页面的数据*/
            HttpSession session = request.getSession();
            session.setAttribute("pageInfo",page);
            model.addAttribute("pageInfo",page);//显示页面的数据、第几页的、及还有多少页之类
            model.addAttribute("bidSort",bidInfoList);
        }catch (Exception e){
            e.printStackTrace();
        }

        //逻辑分页
        /*PageModel<LoanInfo> pageInfo = new PageModel<>();
        pageInfo.setPtype(ptype);
        pageInfo.setCurrentPage(currentPage);
        pageInfo = loanInfoService.queryLoanInfosByTypeAndLogicPage(pageInfo);
        model.addAttribute("pageInfo",pageInfo);*/
        return "loan";
    }


    @GetMapping("/loanInfo")
    public String loanInfo(HttpServletRequest request,Integer id,Model model,@RequestParam(value="currentPage",defaultValue="1",required=false)Integer currentPage) {
            //后台判断页码合理性
            if (currentPage < 1) {
                currentPage = 1;
            }
            LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);
            PageModel pageInfo = bidInfoService.queryBidInfoByLoanId(id, currentPage);

            model.addAttribute("loanInfo", loanInfo);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("id", id);

        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            model.addAttribute("balance","请登陆后再查看余额");
        }else {
            FinanceAccount financeAccount = financeService.queryFinanceAccountByUserId(user.getId());
            if (!ObjectUtils.allNotNull(financeAccount)) {
                model.addAttribute("balance","系统繁忙,请稍后查询,如有疑问请拨打400-699-2121");
            }else {
                model.addAttribute("balance",financeAccount.getAvailableMoney());
            }
        }
            return "loanInfo";

    }

    /**
     * 下一页
     * @param request 拿到session session中有分页模型
     * @param model
     * @return
     */
    @GetMapping("/down")
    public String downPage(HttpServletRequest request,Model  model){
        HttpSession session = request.getSession();
        PageModel pageInfo =(PageModel) session.getAttribute("pageInfo");
        //页码合理校验
        /*if(pageInfo.getCurrentPage()<=1){
            pageInfo.setCurrentPage(1);
        }*/
        try {
        //数据总条数
        Integer totalCount = loanInfoService.queryLoanInfoTotalCount(pageInfo.getPtype());
        pageInfo.setTotalSize(totalCount);
        //总页数
        double tc = totalCount.doubleValue();
        Double num = Math.ceil(tc / pageInfo.getPageSize());//向上取整,除不尽的时候都向上取整
        Integer totalPage = Integer.valueOf(num.intValue());
        pageInfo.setTotalPage(totalPage);
        if(pageInfo.getCurrentPage()>=totalPage){
            pageInfo.setCurrentPage(totalPage);
        }else {
            pageInfo.setCurrentPage(pageInfo.getCurrentPage()+1);
        }
            pageInfo = loanInfoService.queryLoanInfosByTypeAndPage2(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("pageInfo",pageInfo);
        session.setAttribute("pageInfo",pageInfo);
        return "loan";
    }


    /**
     * 下一页
     * @param request 拿到session session中有分页模型
     * @param model
     * @return
     */
    @GetMapping("/up")
    public String upPage(HttpServletRequest request,Model  model){
        HttpSession session = request.getSession();
        PageModel pageInfo =(PageModel) session.getAttribute("pageInfo");
        //页码合理校验
        if(pageInfo.getCurrentPage()<=1){
            pageInfo.setCurrentPage(1);
        }else {
            pageInfo.setCurrentPage(pageInfo.getCurrentPage()-1);
        }
        try {
        //数据总条数
        Integer totalCount = loanInfoService.queryLoanInfoTotalCount(pageInfo.getPtype());
        pageInfo.setTotalSize(totalCount);
        //总页数
        double tc = totalCount.doubleValue();
        Double num = Math.ceil(tc / pageInfo.getPageSize());//向上取整,除不尽的时候都向上取整
        Integer totalPage = Integer.valueOf(num.intValue());
        pageInfo.setTotalPage(totalPage);
       /* if(pageInfo.getCurrentPage()>=totalPage){
            pageInfo.setCurrentPage(totalPage);
        }else {
            pageInfo.setCurrentPage(pageInfo.getCurrentPage()+1);
        }*/

            pageInfo = loanInfoService.queryLoanInfosByTypeAndPage2(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("pageInfo",pageInfo);
        session.setAttribute("pageInfo",pageInfo);
        return "loan";
    }

    /**
     * 去首页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/first")
    public String firstPage(HttpServletRequest request,Model  model){
        HttpSession session = request.getSession();
        PageModel pageInfo =(PageModel) session.getAttribute("pageInfo");
        try {
            //数据总条数
            Integer totalCount = loanInfoService.queryLoanInfoTotalCount(pageInfo.getPtype());
            pageInfo.setTotalSize(totalCount);
            //总页数
            double tc = totalCount.doubleValue();
            Double num = Math.ceil(tc / pageInfo.getPageSize());//向上取整,除不尽的时候都向上取整
            Integer totalPage = Integer.valueOf(num.intValue());
            pageInfo.setTotalPage(totalPage);
            pageInfo.setCurrentPage(1);
            pageInfo = loanInfoService.queryLoanInfosByTypeAndPage2(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("pageInfo",pageInfo);
        session.setAttribute("pageInfo",pageInfo);
        return "loan";
    }


    /**
     * 去尾页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/last")
    public String lastPage(HttpServletRequest request,Model  model){
        HttpSession session = request.getSession();
        PageModel pageInfo =(PageModel) session.getAttribute("pageInfo");
        try {
            //数据总条数
            Integer totalCount = loanInfoService.queryLoanInfoTotalCount(pageInfo.getPtype());
            pageInfo.setTotalSize(totalCount);
            //总页数
            double tc = totalCount.doubleValue();
            Double num = Math.ceil(tc / pageInfo.getPageSize());//向上取整,除不尽的时候都向上取整
            Integer totalPage = Integer.valueOf(num.intValue());
            pageInfo.setTotalPage(totalPage);
            pageInfo.setCurrentPage(totalPage);
            pageInfo = loanInfoService.queryLoanInfosByTypeAndPage2(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("pageInfo",pageInfo);
        session.setAttribute("pageInfo",pageInfo);
        return "loan";
    }

}

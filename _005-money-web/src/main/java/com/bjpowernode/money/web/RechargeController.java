package com.bjpowernode.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.bjpowernode.money.config.AlipayConfig;
import com.bjpowernode.money.utils.*;
import entity.BidInfo;
import entity.RechargeRecord;
import entity.User;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.Object;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.RechargeService;
import service.RedisService;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.System.out;

@Controller
public class RechargeController {

    @Reference(interfaceClass = RechargeService.class,timeout = 20000,version = "1.0.0")
    RechargeService rechargeService;

    @Reference(interfaceClass = RedisService.class,timeout = 20000,version = "1.0.0")
    RedisService redisService;

    /**
     * 去充值页面
     * @return
     */
    @GetMapping("loan/page/toRecharge")
    public String recharge(HttpServletRequest request, @RequestParam(name="ReturnUrl",required = true)String ReturnUrl){
        User user = (User) request.getSession().getAttribute("user");
        //登录验证
        if(!ObjectUtils.allNotNull(user)){
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        //实名认证验证
        if(!ObjectUtils.allNotNull(user.getName(),user.getIdCard())){
            return "redirect:/loan/page/realName?ReturnUrl="+ReturnUrl;
        }

        return "toRecharge";
    }

    /**
     * 支付宝支付充值
     * @param rechargeMoney 充值金额
     * @return
     */
    @PostMapping("loan/page/alipay")
    public String alipay(HttpServletRequest request, Double rechargeMoney, Model model){
        String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myCenter";
        User user = (User) request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        if(!ObjectUtils.allNotNull(user.getName(),user.getIdCard())){
            return "redirect:/loan/page/realName?ReturnUrl="+ReturnUrl;
        }
        //生成充值订单
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(user.getId());
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeStatus(0+"");
        rechargeRecord.setRechargeTime(new Date());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Long aLong = redisService.incrementNum();
        rechargeRecord.setRechargeNo(simpleDateFormat.format(new Date())+RandomCode.randomCode(5)+user.getId()+ aLong);
        rechargeRecord.setRechargeDesc("支付宝充值");
        int count = rechargeService.recharge(rechargeRecord);
        if(count!=1){
            model.addAttribute("trade_msg","充值订单生成失败");
            return "toRechargeBack";
        }
        /*return "redirect:/index";*/
        //支付
        //这种重定向的方式很适合，但是get方式传递参数，数据不安全，所以使用post方式请求
       /* return "redirect:http://localhost:9007/_007-money-pay/loan/page/aliPay?out_trade_no="+rechargeRecord.getRechargeNo()+"&total_amount="+rechargeRecord.getRechargeMoney()+"&subject="+rechargeRecord.getRechargeDesc()+"&body="+rechargeRecord.getRechargeDesc();*/
        //采用ali的方式，通过页面跳转，参数通过form表单传递，post
        model.addAttribute("rechargeRecord",rechargeRecord);
        return "toAliPay";
    }

    /**
     * 功能：支付宝服务器同步通知页面
     * 接收支付宝调用同步方法，返回的订单基本信息
     */
    @GetMapping("loan/page/payBack")
    public String payBack(HttpServletRequest request) throws UnsupportedEncodingException {
        User user = (User) request.getSession().getAttribute("user");
        //获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = false; //调用SDK验证签名
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            //发短信？页面？
            return "404";
        }

        //——请在这里编写您的程序（以下代码仅作参考）——
        if (signVerified) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");

            /* out.println("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);*/

            //顺便查询订单交易状态，应该交给定时器，但这里顺便查询一下，大概率会是成功，就可以有效的减轻定时器压力
            System.out.println("----------------------queryPayOrderInfo--------------------------");
            String result = "{\n" +
                    "\t\"alipay_trade_query_response\": {\n" +
                    "\t\t\"code\": \"10000\",\n" +
                    "\t\t\"msg\": \"Success\",\n" +
                    "\t\t\"buyer_logon_id\": \"yjg***@sandbox.com\",\n" +
                    "\t\t\"buyer_pay_amount\": \"0.00\",\n" +
                    "\t\t\"buyer_user_id\": \"2088622987504939\",\n" +
                    "\t\t\"buyer_user_type\": \"PRIVATE\",\n" +
                    "\t\t\"invoice_amount\": \"0.00\",\n" +
                    "\t\t\"out_trade_no\": \"202281015243914\",\n" +
                    "\t\t\"point_amount\": \"0.00\",\n" +
                    "\t\t\"receipt_amount\": \"0.00\",\n" +
                    "\t\t\"send_pay_date\": \"2022-08-10 15:25:53\",\n" +
                    "\t\t\"total_amount\": \"0.01\",\n" +
                    "\t\t\"trade_no\": \"2022081022001404930501941773\",\n" +
                    "\t\t\"trade_status\": \"TRADE_SUCCESS\"\n" +
                    "\t},\n" +
                    "\t\"sign\": \"WpSNlBOq1wql+FULtlJzdCSYK9nq4pQoSy2Hrg6OgWmAlPBIMm28X8OfCwrH+M+bkbV9UKHIT9BeCBJuM07KZzQctQhdFYeijudQRP9gzMhLYxYrUXMpHtIzV4ydKGZZTyb4SRMathQXIYESAT8q/WjvYe0POzaHGOaE1o5hI+1u5sKMUVBR1yMRpH8HtKZXuxpE3FQzfyGhe8cE/ouWQtt+nxTwjilAGz9q86va0ahpATR3r75pxDjhcPyC6NifNUKbUCNYfH6xeboJ8zISGPIedkgJADGqCXmDVW7OUUU9OHYSp/9ybxFrms3QZMaJruqKh3xYU7FyGvbDFhoHEw==\"\n" +
                    "}";
            try {
                result = HttpClientUtil.doGet("http://localhost:9007/_007-money-pay/loan/page/queryOrder?out_trade_no=" + out_trade_no);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //解析返回值
            //拿到状态码，判断
            JSONObject jsonObject = JSONObject.parseObject(result).getJSONObject("alipay_trade_query_response");
            String code = jsonObject.getString("code");
            //交易状态码不是10000的情况
            if (!StringUtils.equals("10000", code)) {
                //发短信，反馈用户
                String content = "您有一笔充值订单请尽快支付，如已支付请忽略，如有疑问请联系客服：400-888-9999";
                SendMessages.SendMessages(user.getPhone(), content);
                /*return "redirect:/loan/myCenter";*/
            }
            //再判断交易状态
            String trade_status = jsonObject.getString("trade_status");
            //交易状态信息不是成功的情况
            if (StringUtils.equals(trade_status, "WAIT_BUYER_PAY")) {
                //等待买家支付，发短信，催促用户支付订单
                String content = "您有一笔充值订单等待支付，请在10分钟内完成支付，超时订单将自动作废，如有疑问请联系客服：400-888-9999";
                SendMessages.SendMessages(user.getPhone(), content);
                /*return "redirect:/loan/myCenter";*/
            }
            //交易关闭
            if (StringUtils.equals(trade_status, "TRADE_CLOSED")) {
                //交易失败，将订单状态改为2
                rechargeService.rechargeFail(out_trade_no);
                //发短信，反馈用户
                String content = "您有一笔充值订单支付失败，订单已取消，如是本人操作请忽略，如有疑问请联系客服：400-888-9999";
                SendMessages.SendMessages(user.getPhone(), content);
            }
            //交易成功
            if (StringUtils.equals(trade_status, "TRADE_SUCCESS")) {
                //查询订单信息
                RechargeRecord rechargeRecord = rechargeService.queryRechargeRecordByRechargeNo(out_trade_no);
                //判断订单是否丢失
                if (!ObjectUtils.allNotNull(rechargeRecord) && Double.valueOf(total_amount) >= 100000) {
                    if (ObjectUtils.allNotNull(user)) {
                        //模拟创建订单 ， 需要用户id、订单号、交易金额、交易时间、交易说明
                        rechargeRecord.setUid(user.getId());
                        rechargeRecord.setRechargeNo(out_trade_no);
                        rechargeRecord.setRechargeMoney(Double.valueOf(total_amount));
                        rechargeRecord.setRechargeTime(new Date());
                        rechargeRecord.setRechargeDesc("支付宝充值");
                        rechargeService.recharge(rechargeRecord);
                        rechargeRecord = rechargeService.queryRechargeRecordByRechargeNo(out_trade_no);
                    }
                }
                //比较订单金额和实际支付金额,以实际支付金额为准，修改订单充值金额
                if (rechargeRecord.getRechargeMoney() != Double.valueOf(total_amount)) {
                    rechargeService.modifyRechargeMoney(Double.valueOf(total_amount), rechargeRecord.getRechargeNo());
                }

                //订单完好，将状态修改为1 ，将充值金额加进用户账户
                out.println("========="+rechargeRecord.getVersion()+"========");
                boolean b = rechargeService.rechargeSuccess(rechargeRecord);
                if (!b) {
                    //给用户反馈，打消用户疑虑
                    String content = "您的充值订单已成功支付，预计在24h内到账，请注意查收，如有疑问请联系客服：400-888-9999";
                    SendMessages.SendMessages(user.getPhone(), content);
                }
                //已到账，反馈用户，回到小金库
                String content = "您的充值金额已到账，请注意查收，如有疑问请联系客服：400-888-9999";
                /*String s = SendMessages.SendMessages(user.getPhone(), content);*/
                /*out.println(s);*/
                return "redirect:/loan/myCenter";
            }
        } else {

            out.println("验签失败");

        }


        return null;
    }

    /**
     *微信支付充值
     * @param rechargeMoney 充值金额
     * @return
     */
    @PostMapping("loan/page/weixinpay")
    public String weixinpay(Double rechargeMoney){
        out.println("-------weixinpay-------"+rechargeMoney);
        return "";
    }

    /**
     * 用户全部充值记录带分页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myRecharge")
    public String myRecharge(HttpServletRequest request,Model model){
        User user = (User) request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myRecharge";
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        PageModel<RechargeRecord> page = new PageModel<>();

        //封装当前页数
        page.setCurrentPage(1);
        //每页显示记录条数
        page.setPageSize(6);
        //封装用户id
        page.setUserId(user.getId());

        //查询总记录数
        Integer totalCount = rechargeService.queryRechargeRecordCount(user.getId());
        //总页数
        double tc = totalCount.doubleValue();
        Double num = Math.ceil(tc / 6);//向上取整,除不尽的时候都向上取整
        Integer totalPage = Integer.valueOf(num.intValue());
        page = rechargeService.queryRechargeRecordByPage(page);
        page.setTotalSize(totalCount);
        page.setTotalPage(totalPage);
        request.getSession().setAttribute("pageInfo",page);
        model.addAttribute("pageInfo",page);
        return "myRecharge";
    }

    /**
     * 用户全部充值记录带分页--首页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myRecharge/firstPage")
    public String firstPage(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myRecharge";
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        PageModel<RechargeRecord> pageInfo = (PageModel<RechargeRecord>)request.getSession().getAttribute("pageInfo");
        pageInfo.setCurrentPage(1);
        pageInfo = rechargeService.queryRechargeRecordByPage(pageInfo);
        request.getSession().setAttribute("pageInfo",pageInfo);
        model.addAttribute("pageInfo",pageInfo);
        return "myRecharge";
    }

    /**
     * 用户全部充值记录带分页--上一页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myRecharge/upPage")
    public String upPage(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myRecharge";
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        PageModel<RechargeRecord> pageInfo =(PageModel) request.getSession().getAttribute("pageInfo");
        if(pageInfo.getCurrentPage()>1){
            pageInfo.setCurrentPage(pageInfo.getCurrentPage()-1);
        }
        pageInfo = rechargeService.queryRechargeRecordByPage(pageInfo);
        request.getSession().setAttribute("pageInfo",pageInfo);
        model.addAttribute("pageInfo",pageInfo);
        return "myRecharge";
    }

    /**
     * 用户全部充值记录带分页--下一页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myRecharge/nextPage")
    public String nextPage(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myRecharge";
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        PageModel<RechargeRecord> pageInfo =(PageModel) request.getSession().getAttribute("pageInfo");
        System.out.println(pageInfo);
        if(pageInfo.getCurrentPage()<pageInfo.getTotalPage()){
            pageInfo.setCurrentPage(pageInfo.getCurrentPage()+1);
        }
        pageInfo = rechargeService.queryRechargeRecordByPage(pageInfo);
        System.out.println(pageInfo);
        request.getSession().setAttribute("pageInfo",pageInfo);
        model.addAttribute("pageInfo",pageInfo);
        return "myRecharge";
    }

    /**
     * 用户全部充值记录带分页--尾页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myRecharge/lastPage")
    public String lastPage(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myRecharge";
            return "redirect:/loan/page/login?Return="+ReturnUrl;
        }
        PageModel<RechargeRecord> pageInfo = (PageModel)request.getSession().getAttribute("pageInfo");
        pageInfo.setCurrentPage(pageInfo.getTotalPage());
        pageInfo = rechargeService.queryRechargeRecordByPage(pageInfo);
        request.getSession().setAttribute("pageInfo",pageInfo);
        model.addAttribute("pageInfo",pageInfo);
        return "myRecharge";
    }
}

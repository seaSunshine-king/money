package com.bjpowernode.money.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.container.page.Page;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.money.utils.HttpClientUtil;
import com.bjpowernode.money.utils.PageModel;
import com.bjpowernode.money.utils.RandomCode;
import com.bjpowernode.money.utils.Result;
import entity.BidInfo;
import entity.LoanInfo;
import entity.RechargeRecord;
import entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
public class UserController {

    @Reference(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0")
    UserService userService;

    @Reference(interfaceClass = RedisService.class,timeout = 20000,version = "1.0.0")
    RedisService redisService;

    @Reference(interfaceClass = BidInfoService.class,timeout = 20000,version = "1.0.0")
    BidInfoService bidInfoService;

    @Reference(interfaceClass = LoanInfoService.class,timeout = 20000,version = "1.0.0")
    LoanInfoService loanInfoService;

    @Reference(interfaceClass = RechargeService.class,timeout = 20000,version = "1.0.0")
    RechargeService rechargeService;

    @GetMapping("loan/page/register")
    public String register(@RequestParam(name="ReturnUrl",required = true)String ReturnUrl,Model model){
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "register";
    }

    @GetMapping("loan/page/phone")
    @ResponseBody
    public Object registerPhone(@RequestParam(name="phone",required = true) String phone){
        int count = userService.queryUserByPhone(phone);
        if(count>=1){
            return  Result.error("手机号码已注册");
        }
        return Result.success();
    }

    @PostMapping("loan/page/registerSubmit")
    @ResponseBody
    public Object registerSubmit(HttpServletRequest request,@RequestParam(name = "phone",required = true) String phone, @RequestParam(name = "loginPassword",required = true) String loginPassword, @RequestParam(name = "verifyCode",required = true) String verifyCode){
        String code = redisService.pop(phone);
        if(!StringUtils.equals(code,verifyCode)){
            return Result.error("验证码错误");
        }
        User user = userService.addUser(phone,loginPassword);
        if(user==null){
            return Result.error("注册失败");
        }
        HttpSession session = request.getSession();
        session.setAttribute("user",user);
        return Result.success("注册成功");
    }

    /**
     * 注册手机验证码
     * @return
     */
    @GetMapping("loan/page/verifyCode")
    @ResponseBody
    public Object verifyCode(@RequestParam(name = "phone",required = true)String phone){
        //生成随机验证码
        String code = RandomCode.randomCode(4);
        //请求地址 url
        String url = "https://way.jd.com/chuangxin/dxjk";
        //集合用来装发短信需要的参数
        Map<String,String> map = new HashMap<>();
        //申请的appkey
        map.put("appkey","01a16cb8a1a18177c87867a2a07ba558");
        //需要发送的手机号码
        map.put("mobile",phone);
        //发送内容
        map.put("content","【创信】你的验证码是：\"+code+\"，3分钟内有效！");
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

        //发送短信
        try {
            /*result = HttpClientUtil.doGet(url,map);*/
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统维护中");
        }

        //将result转换成json格式
        JSONObject jsonObject = JSONObject.parseObject(result);
        //将发送短信返回的状态码拿到
        String result_code = jsonObject.getString("code");

        /**
         *校验状态码
         * 10000	查询成功
         * 如果状态码不等于10000，代码短信发送失败
         */
        /*System.out.println(result);
        System.out.println(result_code);*/
        if(!StringUtils.equals("10000",result_code)){
            return Result.error("系统异常");
        }
        /**
         * 拿到返回json中的提示信息result
         * "result": {
         *         "ReturnStatus": "Success",
         *         "Message": "ok",
         *         "RemainPoint": 780765,
         *         "TaskID": 82272611,
         *         "SuccessCounts": 1
         *     }
         */
        String json = jsonObject.getString("result");
        //将json字符串转换成json格式
        JSONObject result_json = JSONObject.parseObject(json);
        //拿到其中的返回状态ReturnStatus
        String returnStatus = result_json.getString("ReturnStatus");
        //如果返回状态不是Success也代表失败
        if(!StringUtils.equals("Success",returnStatus)){
            return Result.error("短信发送失败");
        }
        //走到这一步代表短信发送成功，把验证码放进缓存
        redisService.push(phone,code);
        return Result.success(code);
    }

    @GetMapping("loan/page/realName")
    public String realName(@RequestParam(name="ReturnUrl",required = true)String ReturnUrl,Model model){
        model.addAttribute("ReturnUrl",ReturnUrl);
        return "realName";
    }

    @GetMapping("loan/page/realNameAndIdCard")
    @ResponseBody
    public Object realNameAndIdCard(@RequestParam(name="name",required = true)String name,@RequestParam(name = "idCard",required = true)String idCard){
        Map<String, String> map = new HashMap<>();
        //请求地址 url
        String url = "https://way.jd.com/youhuoBeijing/test";
        //申请的appkey
        map.put("appkey","01a16cb8a1a18177c87867a2a07ba558");
        //需要发送的手机号码
        map.put("cardNo",idCard);
        map.put("realName",name);
        //模拟报文
        String result = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"error_code\": 0,\n" +
                "        \"reason\": \"成功\",\n" +
                "        \"result\": {\n" +
                "            \"realname\": \"乐天磊\",\n" +
                "            \"idcard\": \"350721197702134399\",\n" +
                "            \"isok\": true\n" +
                "        }\n" +
                "    }\n" +
                "}";

        //发送查询
        try {
            /*result = HttpClientUtil.doGet(url,map);*/
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统维护中");
        }

        //将result转换成json格式
        JSONObject jsonObject = JSONObject.parseObject(result);
        //将发送短信返回的状态码拿到
        String result_code = jsonObject.getString("code");
        System.out.println(result_code);

        /**
         *校验状态码
         * 10000	查询成功
         * 如果状态码不等于10000，代表失败
         */
        /*System.out.println(result);
        System.out.println(result_code);*/
        if(!StringUtils.equals("10000",result_code)){
            return Result.error("系统异常");
        }
        /**
         * 拿到返回json中的提示信息result
         * result	object	{...}	返回结果
         * idcard	string	350721197702134399	身份证
         * isok	boolean	true	true：匹配 false：不匹配
         * realname	string	乐天磊	姓名
         * reason	string	成功	查询结果文字描述
         * error_code	number	0	状态码是否扣费 0扣费 其他不扣费
         */
        String json = jsonObject.getString("result");
        //将json字符串转换成json格式
        JSONObject result_json = JSONObject.parseObject(json);
        String result_result = result_json.getString("result");
        JSONObject jsonObject1 = JSONObject.parseObject(result_result);
        //拿到其中的返回匹配结果isok
        String result1Isok = jsonObject1.getString("isok");

        System.out.println(result1Isok);
        //如果返回状态不是Success也代表失败
        if(!StringUtils.equals("true",result1Isok)){
            return Result.error("姓名与身份证号码不匹配");
        }
        //走到这一步代表短信发送成功，把验证码放进缓存
        redisService.push("name",name);
        redisService.push("idCard",idCard);
        return Result.success("匹配成功");

    }

    @PostMapping("loan/page/realNameSubmit")
    @ResponseBody
    public Object realNameSubmit(@RequestParam(name="phone",required = true)String phone,
                                 @RequestParam(name="realName",required = true)String realName,
                                 @RequestParam(name="idCard",required = true)String idCard,
                                 @RequestParam(name="code",required = true)String code,
                                 HttpServletRequest request){
        String str = redisService.pop(phone);
        if(!StringUtils.equals(str,code)){
            return Result.error("验证码错误");
        }
        User user = (User) request.getSession().getAttribute("user");
        user.setName(realName);
        user.setIdCard(idCard);

        int count = userService.addUserInfo(user);
        if(count==0){
            return Result.error("实名认证失败");
        }
        request.getSession().setAttribute("user",user);
        return Result.success("实名认证成功");
    }


    @PostMapping("loan/page/loginSubmit")
    @ResponseBody
    public Object loginSubmit(HttpServletRequest request,@RequestParam(name="phone",required = true) String phone,@RequestParam(name="loginPassword",required = true) String loginPassword){

        User user = userService.quiryUserByPhone(phone);
        if(!ObjectUtils.allNotNull(user)|| !StringUtils.equals(user.getLoginPassword(),loginPassword)){
            return Result.error("账号或密码不正确");
        }
        //*System.out.println(user.getFinanceAccount().getAvailableMoney());
        HttpSession session = request.getSession();
        session.setAttribute("user",user);

        return Result.success();

    }

     /**
     * 登录
     * @param model
     * @return
     */
   @GetMapping("loan/page/login")
   public String login(Model model,@RequestParam(name="ReturnUrl",required = true)String ReturnUrl){
       //动力金融网历史年化收益率  （产品平均利率）
       Double loanInfoHistoryRateAvg = loanInfoService.queryLoanInfoHistoryRateAvg();
       model.addAttribute("loanInfoHistoryRateAvg",loanInfoHistoryRateAvg);

       //平台用户数 （注册总人数）
       Long userCount = userService.queryUserCount();
       model.addAttribute("userCount",userCount);

       //累计成交额  （投资总金额）
       Double bidMoneySum = bidInfoService.queryBidMoneySum();
       model.addAttribute("bidMoneySum",bidMoneySum);

       //优选投资年收益lv
       Double loanInfoHistoryRateAvgByType = loanInfoService.queryLoanInfoHistoryRateAvgByType(1);
       System.out.println("----"+loanInfoHistoryRateAvgByType+"-----");
       model.addAttribute("loanInfoHistoryRateAvgByType",loanInfoHistoryRateAvgByType);
       model.addAttribute("ReturnUrl",ReturnUrl);
       return "login";
   }


    @GetMapping("loan/logout")
    public String logout(HttpServletRequest request,@RequestParam(name="ReturnUrl",required = true)String ReturnUrl){

        HttpSession session = request.getSession();
        session.invalidate();
        /*return "/_005-money-web/index";*/
        return "redirect:"+ReturnUrl;
    }

    /**
     * 头像上传
     * @param request
     * @param file
     * @return
     */
    @PostMapping("loan/page/uploadHeader")
    public String uploadHeader(Model model,HttpServletRequest request,@RequestParam(name="file",required = true) MultipartFile file){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            model.addAttribute("error", "请登录后上传头像");
        }else {
            String fileOldName = file.getOriginalFilename();
            String fileType = fileOldName.substring(fileOldName.lastIndexOf('.') + 1);
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String fileName = uuid + "." + fileType;
            /*String realPath = request.getServletContext().getRealPath("/im");*/
            /*System.out.println(realPath);*/
            File newFile = new File("E:/JavaProjects/money/_005-money-web/src/main/resources/static/images/uploadHeader/" + fileName);
            try {
                file.transferTo(newFile);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error","头像上传失败");
            }
            int count = userService.uploadHeader(user.getId(), fileName);
            if (count == 0) {
                model.addAttribute("error","头像上传失败");
                return "loan/myCenter";
            }
            user.setHeaderImage(fileName);
            request.getSession().setAttribute("user", user);
            return "redirect:/loan/myCenter";
        }
        return  "redirect:/loan/myCenter";
    }

    /**
     * 全部投资页面分页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myInvest")
    public String myInvest(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myInvest";
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        PageModel<BidInfo> page = new PageModel<>();

        //封装当前页数
        page.setCurrentPage(1);
        //每页显示记录条数
        page.setPageSize(6);
        //封装用户id
        page.setUserId(user.getId());

        //查询总记录数
        Integer totalCount = bidInfoService.queryBidInfoCount(user.getId());
        //总页数
        double tc = totalCount.doubleValue();
        Double num = Math.ceil(tc / 6);//向上取整,除不尽的时候都向上取整
        Integer totalPage = Integer.valueOf(num.intValue());
        page = bidInfoService.queryBidInfoByUserIdPage(page);
        page.setTotalSize(totalCount);
        page.setTotalPage(totalPage);
        request.getSession().setAttribute("pageInfo",page);
        model.addAttribute("pageInfo",page);
        return "myInvest";
    }

    /**
     * 全部投资页面分页--首页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myInvest/firstPage")
    public String firstPage(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myInvest";
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        PageModel<BidInfo> pageInfo = (PageModel<BidInfo>)request.getSession().getAttribute("pageInfo");
        pageInfo.setCurrentPage(1);
        pageInfo = bidInfoService.queryBidInfoByUserIdPage(pageInfo);
        request.getSession().setAttribute("pageInfo",pageInfo);
        model.addAttribute("pageInfo",pageInfo);
        return "myInvest";
    }

    /**
     *全部投资信息--上一页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myInvest/upPage")
    public String upPage(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myInvest";
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        PageModel<BidInfo> pageInfo =(PageModel) request.getSession().getAttribute("pageInfo");
        if(pageInfo.getCurrentPage()>1){
            pageInfo.setCurrentPage(pageInfo.getCurrentPage()-1);
        }
        pageInfo = bidInfoService.queryBidInfoByUserIdPage(pageInfo);
        request.getSession().setAttribute("pageInfo",pageInfo);
        model.addAttribute("pageInfo",pageInfo);
        return "myInvest";
    }

    /**
     *全部投资信息--下一页
     * @param request
     * @param model
     * @return
     */
    @GetMapping("loan/myInvest/nextPage")
    public String nextPage(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myInvest";
            return "redirect:/loan/page/login?ReturnUrl="+ReturnUrl;
        }
        PageModel<BidInfo> pageInfo =(PageModel) request.getSession().getAttribute("pageInfo");
        System.out.println(pageInfo);
        if(pageInfo.getCurrentPage()<pageInfo.getTotalPage()){
            pageInfo.setCurrentPage(pageInfo.getCurrentPage()+1);
        }
        pageInfo = bidInfoService.queryBidInfoByUserIdPage(pageInfo);
        System.out.println(pageInfo);
        request.getSession().setAttribute("pageInfo",pageInfo);
        model.addAttribute("pageInfo",pageInfo);
        return "myInvest";
    }

    @GetMapping("loan/myInvest/lastPage")
    public String lastPage(HttpServletRequest request,Model model){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            String ReturnUrl = "http://localhost:9005/_005-money-web/loan/myInvest";
            return "redirect:/loan/page/login?Return="+ReturnUrl;
        }
        PageModel<BidInfo> pageInfo = (PageModel)request.getSession().getAttribute("pageInfo");
        pageInfo.setCurrentPage(pageInfo.getTotalPage());
        pageInfo = bidInfoService.queryBidInfoByUserIdPage(pageInfo);
        request.getSession().setAttribute("pageInfo",pageInfo);
        model.addAttribute("pageInfo",pageInfo);
        return "myInvest";
    }




}

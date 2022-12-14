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
            return  Result.error("?????????????????????");
        }
        return Result.success();
    }

    @PostMapping("loan/page/registerSubmit")
    @ResponseBody
    public Object registerSubmit(HttpServletRequest request,@RequestParam(name = "phone",required = true) String phone, @RequestParam(name = "loginPassword",required = true) String loginPassword, @RequestParam(name = "verifyCode",required = true) String verifyCode){
        String code = redisService.pop(phone);
        if(!StringUtils.equals(code,verifyCode)){
            return Result.error("???????????????");
        }
        User user = userService.addUser(phone,loginPassword);
        if(user==null){
            return Result.error("????????????");
        }
        HttpSession session = request.getSession();
        session.setAttribute("user",user);
        return Result.success("????????????");
    }

    /**
     * ?????????????????????
     * @return
     */
    @GetMapping("loan/page/verifyCode")
    @ResponseBody
    public Object verifyCode(@RequestParam(name = "phone",required = true)String phone){
        //?????????????????????
        String code = RandomCode.randomCode(4);
        //???????????? url
        String url = "https://way.jd.com/chuangxin/dxjk";
        //???????????????????????????????????????
        Map<String,String> map = new HashMap<>();
        //?????????appkey
        map.put("appkey","01a16cb8a1a18177c87867a2a07ba558");
        //???????????????????????????
        map.put("mobile",phone);
        //????????????
        map.put("content","?????????????????????????????????\"+code+\"???3??????????????????");
        //????????????
        String result = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 0,\n" +
                "    \"msg\": \"????????????\",\n" +
                "    \"result\": {\n" +
                "        \"ReturnStatus\": \"Success\",\n" +
                "        \"Message\": \"ok\",\n" +
                "        \"RemainPoint\": 1052849,\n" +
                "        \"TaskID\": 69373036,\n" +
                "        \"SuccessCounts\": 1\n" +
                "    },\n" +
                "    \"requestId\": \"7777cc0f5afe4cda957146c7e747229f\"\n" +
                "}";

        //????????????
        try {
            /*result = HttpClientUtil.doGet(url,map);*/
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("???????????????");
        }

        //???result?????????json??????
        JSONObject jsonObject = JSONObject.parseObject(result);
        //???????????????????????????????????????
        String result_code = jsonObject.getString("code");

        /**
         *???????????????
         * 10000	????????????
         * ????????????????????????10000???????????????????????????
         */
        /*System.out.println(result);
        System.out.println(result_code);*/
        if(!StringUtils.equals("10000",result_code)){
            return Result.error("????????????");
        }
        /**
         * ????????????json??????????????????result
         * "result": {
         *         "ReturnStatus": "Success",
         *         "Message": "ok",
         *         "RemainPoint": 780765,
         *         "TaskID": 82272611,
         *         "SuccessCounts": 1
         *     }
         */
        String json = jsonObject.getString("result");
        //???json??????????????????json??????
        JSONObject result_json = JSONObject.parseObject(json);
        //???????????????????????????ReturnStatus
        String returnStatus = result_json.getString("ReturnStatus");
        //????????????????????????Success???????????????
        if(!StringUtils.equals("Success",returnStatus)){
            return Result.error("??????????????????");
        }
        //??????????????????????????????????????????????????????????????????
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
        //???????????? url
        String url = "https://way.jd.com/youhuoBeijing/test";
        //?????????appkey
        map.put("appkey","01a16cb8a1a18177c87867a2a07ba558");
        //???????????????????????????
        map.put("cardNo",idCard);
        map.put("realName",name);
        //????????????
        String result = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"????????????\",\n" +
                "    \"result\": {\n" +
                "        \"error_code\": 0,\n" +
                "        \"reason\": \"??????\",\n" +
                "        \"result\": {\n" +
                "            \"realname\": \"?????????\",\n" +
                "            \"idcard\": \"350721197702134399\",\n" +
                "            \"isok\": true\n" +
                "        }\n" +
                "    }\n" +
                "}";

        //????????????
        try {
            /*result = HttpClientUtil.doGet(url,map);*/
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("???????????????");
        }

        //???result?????????json??????
        JSONObject jsonObject = JSONObject.parseObject(result);
        //???????????????????????????????????????
        String result_code = jsonObject.getString("code");
        System.out.println(result_code);

        /**
         *???????????????
         * 10000	????????????
         * ????????????????????????10000???????????????
         */
        /*System.out.println(result);
        System.out.println(result_code);*/
        if(!StringUtils.equals("10000",result_code)){
            return Result.error("????????????");
        }
        /**
         * ????????????json??????????????????result
         * result	object	{...}	????????????
         * idcard	string	350721197702134399	?????????
         * isok	boolean	true	true????????? false????????????
         * realname	string	?????????	??????
         * reason	string	??????	????????????????????????
         * error_code	number	0	????????????????????? 0?????? ???????????????
         */
        String json = jsonObject.getString("result");
        //???json??????????????????json??????
        JSONObject result_json = JSONObject.parseObject(json);
        String result_result = result_json.getString("result");
        JSONObject jsonObject1 = JSONObject.parseObject(result_result);
        //?????????????????????????????????isok
        String result1Isok = jsonObject1.getString("isok");

        System.out.println(result1Isok);
        //????????????????????????Success???????????????
        if(!StringUtils.equals("true",result1Isok)){
            return Result.error("?????????????????????????????????");
        }
        //??????????????????????????????????????????????????????????????????
        redisService.push("name",name);
        redisService.push("idCard",idCard);
        return Result.success("????????????");

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
            return Result.error("???????????????");
        }
        User user = (User) request.getSession().getAttribute("user");
        user.setName(realName);
        user.setIdCard(idCard);

        int count = userService.addUserInfo(user);
        if(count==0){
            return Result.error("??????????????????");
        }
        request.getSession().setAttribute("user",user);
        return Result.success("??????????????????");
    }


    @PostMapping("loan/page/loginSubmit")
    @ResponseBody
    public Object loginSubmit(HttpServletRequest request,@RequestParam(name="phone",required = true) String phone,@RequestParam(name="loginPassword",required = true) String loginPassword){

        User user = userService.quiryUserByPhone(phone);
        if(!ObjectUtils.allNotNull(user)|| !StringUtils.equals(user.getLoginPassword(),loginPassword)){
            return Result.error("????????????????????????");
        }
        //*System.out.println(user.getFinanceAccount().getAvailableMoney());
        HttpSession session = request.getSession();
        session.setAttribute("user",user);

        return Result.success();

    }

     /**
     * ??????
     * @param model
     * @return
     */
   @GetMapping("loan/page/login")
   public String login(Model model,@RequestParam(name="ReturnUrl",required = true)String ReturnUrl){
       //????????????????????????????????????  ????????????????????????
       Double loanInfoHistoryRateAvg = loanInfoService.queryLoanInfoHistoryRateAvg();
       model.addAttribute("loanInfoHistoryRateAvg",loanInfoHistoryRateAvg);

       //??????????????? ?????????????????????
       Long userCount = userService.queryUserCount();
       model.addAttribute("userCount",userCount);

       //???????????????  ?????????????????????
       Double bidMoneySum = bidInfoService.queryBidMoneySum();
       model.addAttribute("bidMoneySum",bidMoneySum);

       //?????????????????????lv
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
     * ????????????
     * @param request
     * @param file
     * @return
     */
    @PostMapping("loan/page/uploadHeader")
    public String uploadHeader(Model model,HttpServletRequest request,@RequestParam(name="file",required = true) MultipartFile file){
        User user = (User)request.getSession().getAttribute("user");
        if(!ObjectUtils.allNotNull(user)){
            model.addAttribute("error", "????????????????????????");
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
                model.addAttribute("error","??????????????????");
            }
            int count = userService.uploadHeader(user.getId(), fileName);
            if (count == 0) {
                model.addAttribute("error","??????????????????");
                return "loan/myCenter";
            }
            user.setHeaderImage(fileName);
            request.getSession().setAttribute("user", user);
            return "redirect:/loan/myCenter";
        }
        return  "redirect:/loan/myCenter";
    }

    /**
     * ????????????????????????
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

        //??????????????????
        page.setCurrentPage(1);
        //????????????????????????
        page.setPageSize(6);
        //????????????id
        page.setUserId(user.getId());

        //??????????????????
        Integer totalCount = bidInfoService.queryBidInfoCount(user.getId());
        //?????????
        double tc = totalCount.doubleValue();
        Double num = Math.ceil(tc / 6);//????????????,?????????????????????????????????
        Integer totalPage = Integer.valueOf(num.intValue());
        page = bidInfoService.queryBidInfoByUserIdPage(page);
        page.setTotalSize(totalCount);
        page.setTotalPage(totalPage);
        request.getSession().setAttribute("pageInfo",page);
        model.addAttribute("pageInfo",page);
        return "myInvest";
    }

    /**
     * ????????????????????????--??????
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
     *??????????????????--?????????
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
     *??????????????????--?????????
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

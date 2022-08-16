package com.bjpowernode.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.money.mapper.BidInfoMapper;
import com.bjpowernode.money.mapper.FinanceAccountMapper;
import com.bjpowernode.money.mapper.LoanInfoMapper;
import com.bjpowernode.money.mapper.UserMapper;
import com.bjpowernode.money.utils.PageModel;
import com.bjpowernode.money.utils.constant;
import entity.BidInfo;
import entity.FinanceAccount;
import entity.LoanInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import service.BidInfoService;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 投资业务实现类
 */
//暴露服务
@Service(interfaceClass = BidInfoService.class,timeout = 20000,version = "1.0.0")
@Component
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired
    BidInfoMapper bidInfoMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    LoanInfoMapper loanInfoMapper;

    @Autowired
    FinanceAccountMapper financeAccountMapper;

    @Autowired(required = false)
    RedisTemplate redisTemplate;

    Map<Integer,LoanInfo> loanMap = new HashMap<>();

    @Override
    public Double queryBidMoneySum() {

        //设置redisTemplate对象的key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        Double bidMoneySum = (Double) redisTemplate.opsForValue().get(constant.BID_MONEY_SUM);
        if(bidMoneySum==null){
            synchronized (this) {
                bidMoneySum = (Double) redisTemplate.opsForValue().get(constant.BID_MONEY_SUM);
                if (bidMoneySum == null) {
                    System.out.println("--查询数据库--");
                    bidMoneySum = bidInfoMapper.selectBidMoneySum();
                    redisTemplate.opsForValue().set(constant.BID_MONEY_SUM, bidMoneySum, 20, TimeUnit.SECONDS);
                }else {
                    System.out.println("--缓存命中--");
                }
            }
        }else {
            System.out.println("--缓存命中--");
        }
        return bidMoneySum;
    }

    /**
     * 根据产品id查询投资信息
     * @param id 产品id
     * @return
     */
    @Override
    public PageModel<BidInfo> queryBidInfoByLoanId(Integer id, Integer currentPage){
        HashMap<String, Object> map = new HashMap<>();
        PageModel<BidInfo> page = new PageModel<>();

        //封装当前页数
        page.setCurrentPage(currentPage);

        //每页显示的数据
        Integer pageSize = 10;
        page.setPageSize(pageSize);

        //封装总记录数
        Integer totalCount = bidInfoMapper.selectBidInfoCountByLoanId(id);
        page.setTotalSize(totalCount);

        //封装总页数
        double tc = totalCount.doubleValue();
        Double num = Math.ceil(tc / pageSize);//向上取整,除不尽的时候都向上取整
        page.setTotalPage(Integer.valueOf(num.intValue()));

        //后台判断页码合理性
        if(currentPage>page.getTotalPage()&&currentPage!=1){
            currentPage=page.getTotalPage();
        }

        map.put("start", (currentPage - 1) * pageSize);
        map.put("size", page.getPageSize());
        //封装每页显示的数据
        /*logger.debug(map);*/
        map.put("id", id);
        List<BidInfo> lists = bidInfoMapper.selectBidInfoByLoanId(map);
        page.setList(lists);
        return page;
    }

    /**
     * 根据id查询投资信息总条数
     * @param id
     * @return
     */
    @Override
    public Integer queryBidInfoCountByLoanId(Integer id){
        return bidInfoMapper.selectBidInfoCountByLoanId(id);
    }

    /**
     * 投资
     * @param map 参数集合
     * @return
     */
    @Override
    @Transactional
   public Integer invest(Map map){
        /*投资：
         *1、产品剩余可投金额减少
         *2、账户余额减少
         *3、判断产品是否满标（卖光了）
         * 4、添加投资记录*/
        //再进行一次是否在剩余可投金额之内校验
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey((Integer) map.get("loanId"));
        if(((Double)map.get("bidMoney"))>loanInfo.getLeftProductMoney()){
            return constant.BID_MONEY_EXCEED_LEFT_MONEY;
        }
        //判断用户账户余额是否够
        FinanceAccount financeAccount = financeAccountMapper.selectByUserId((Integer) map.get("userId"));
        if(financeAccount.getAvailableMoney()<((Double) map.get("bidMoney"))){
            return constant.ACCOUNT_BALANCE_INSUFFICIENT;
        }
        //1、产品剩余可投金额减少,再检验一遍
        loanInfo = loanInfoMapper.selectByPrimaryKey((Integer) map.get("loanId"));
        if(loanInfo.getLeftProductMoney()<((Double) map.get("bidMoney"))){
            return constant.BID_MONEY_EXCEED_LEFT_MONEY;
        }
        //产品剩余可投金额减少
        LoanInfo loanInfo1 = loanMap.get((Integer) map.get("loanId"));
        int num=0;
        if(loanInfo1==null){
            loanInfo1 = new LoanInfo((Integer) map.get("loanId"));
                num = loanInfoMapper.updateLeftMoneyByBidMoney((Double) map.get("bidMoney"),((Integer)map.get("loanId")));
            loanMap.put((Integer) map.get("loanId"),loanInfo1);
        }else {
            synchronized (loanInfo1){
                num = loanInfoMapper.updateLeftMoneyByBidMoney((Double) map.get("bidMoney"),((Integer)map.get("loanId")));
            }
        }

        if(num==0){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return constant.UPDATE_LOAN_LEFT_MONEY_ERROR;
        }
        //2、账户余额减少
        num = financeAccountMapper.updateAvailableMoneyByBidMoney(((Integer)map.get("userId")),((Double)map.get("bidMoney")));
        if(num==0){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return constant.ACCOUNT_BALANCE_INSUFFICIENT;
        }
        //判断产品是否满标并修改
        loanInfo = loanInfoMapper.selectByPrimaryKey((Integer) map.get("loanId"));
        if(loanInfo.getLeftProductMoney()==0&&loanInfo.getProductStatus()==0){
            num = loanInfoMapper.updateStatus(((Integer) map.get("loanId")), 1,new Date());
            if(num==0){
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return constant.UPDATE_PRODUCT_STATUS_ERROR;
            }
        }
        //添加投资记录
        BidInfo bidInfo = new BidInfo();
        bidInfo.setBidMoney((Double) map.get("bidMoney"));
        bidInfo.setBidStatus(1);
        bidInfo.setLoanId((Integer) map.get("loanId"));
        bidInfo.setBidTime(new Date());
        bidInfo.setUid((Integer) map.get("userId"));
        num= bidInfoMapper.insertSelective(bidInfo);
        if(num==0){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return constant.INSERT_BID_RECORD_ERROR;
        }
        /*Set<ZSetOperations.TypedTuple> bidTop =(Set<ZSetOperations.TypedTuple>) redisTemplate.opsForValue().get("bidTop");
        if(bidTop==null||bidTop.size()==0){

        }*/
       /* redisTemplate.opsForValue().get("bidList")
        if(){}
       redisTemplate.opsForValue().set(((String)map.get("phone")),(Double) map.get("bidMoney"));*/
        return constant.BID_SUCCESS;

    }

    /**
     * 投资排行
     * @return
     */
    @Override
    public List queryBidInfoBySort(Integer ptype){
        //先将此类型产品的产品id查出来
       List<Integer> list =  loanInfoMapper.selectLoanIdByType(ptype);
        return bidInfoMapper.selectByLoanIds(list);
    }

    /**
     * 查询用户最近投资记录
     * 带物理分页
     * @param pageInfo 分页信息
     * @return
     */
    @Override
    public PageModel<BidInfo> queryBidInfoByUserIdPage(PageModel pageInfo){
        Map<String,Object> map = new HashMap<>();
        //从第几条开始，以及每页显示多少条
        map.put("userId",pageInfo.getUserId());
        map.put("start", (pageInfo.getCurrentPage() - 1) * pageInfo.getPageSize());
        map.put("size", pageInfo.getPageSize());
        List<BidInfo> list = bidInfoMapper.selectBidInfoByUserIdPage(map);
        //排序
        list.sort(new Comparator<BidInfo>() {
            @Override
            public int compare(BidInfo o1, BidInfo o2) {
                return o2.getBidTime().compareTo(o1.getBidTime());
            }
        });
        pageInfo.setList(list);
        return pageInfo;
    }

    /**
     * 查询用户最近投资记录
     * @param userId
     * @return
     */
    @Override
    public List<BidInfo> queryBidInfoByUserId(Integer userId){
        return bidInfoMapper.selectBidInfoByUserId(userId);
    }

    /**
     * 查询指定用户投资总记录条数
     * @param userId
     */
    @Override
    public Integer queryBidInfoCount(Integer userId){
       return bidInfoMapper.selectBidInfoCountByUserId(userId);
    }
}

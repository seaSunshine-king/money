package com.bjpowernode.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.money.mapper.LoanInfoMapper;
import com.bjpowernode.money.utils.PageModel;
import com.sun.org.apache.xml.internal.utils.IntVector;
import entity.LoanInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.LoanInfoService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 产品业务实现类
 */
@Slf4j
@Service(interfaceClass = LoanInfoService.class,timeout = 20000,version = "1.0.0")  //暴露服务
@Component //交给spring容器管理
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired //自动装配
    LoanInfoMapper loanInfoMapper;

    /**
     * RedisTemplate:spring 封装了 RedisTemplate 对象来进行对redis的各种操作，它支持所有的 redis 原生的 api
     * :@Autowired(required = false)注入bean的时候如果bean存在，就注入成功，如果没有就忽略跳过，启动不会报错！但是不能直接使用
     */
    @Autowired(required = false) //目的：消除注入失败的红色下划线
    RedisTemplate redisTemplate;

    /**
     *  //动力金融网历史年化收益率
     * @return
     */
    @Override
    public Double queryLoanInfoHistoryRateAvg() {
        //设置redisTemplate对象的key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //从缓存中命中数据
        Double loanInfoHistoryRateAvg = (Double) redisTemplate.opsForValue().get("loanInfoHistoryRateAvg");
        //如果数据结过为空，未命中
        if(loanInfoHistoryRateAvg==null){
            synchronized (this) {
                loanInfoHistoryRateAvg = (Double) redisTemplate.opsForValue().get("loanInfoHistoryRateAvg");
                if(loanInfoHistoryRateAvg==null) {
                    System.out.println("--查询数据库--");
                    //去数据库中查
                    loanInfoHistoryRateAvg = loanInfoMapper.selectLoanInfoHistoryRateAvg();
                    redisTemplate.opsForValue().set("loanInfoHistoryRateAvg", loanInfoHistoryRateAvg, 20, TimeUnit.SECONDS);
                }else {
                    System.out.println("--缓存命中--");
                }
            }
        }else {
            System.out.println("--缓存命中--");
        }
        return loanInfoHistoryRateAvg;
    }

    /**
     * 根据产品类型和数量 查询产品信息
     * @param parasMap
     * @return
     */
    @Override
    public List<LoanInfo> queryLoanInfosByTypeAndNum(Map<String, Object> parasMap) {
        return loanInfoMapper.selectLoanInfosByTypeAndNum(parasMap);
    }

    /**
     * 物理分页实现
     * @param currentPage   去到哪一页
     * @param type  产品类型
     * @return
     * @throws Exception
     */
    @Override
    /*@Transactional(value = "Exception.class")*/
    public PageModel<LoanInfo> queryLoanInfosByTypeAndPage(int currentPage,Integer type) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        PageModel<LoanInfo> page = new PageModel<>();

        //封装当前页数
        page.setCurrentPage(currentPage);

        //每页显示的数据
        Integer pageSize = 9;
        page.setPageSize(pageSize);

        map.put("start", (currentPage - 1) * pageSize);
        map.put("size", page.getPageSize());
        //封装每页显示的数据
        /*logger.debug(map);*/
        map.put("type", type);
        List<LoanInfo> lists = loanInfoMapper.selectLoanInfosByTypeAndPage(map);
        page.setList(lists);
        return page;
    }

    /**
     * 查询产品记录总条数
     * @param type 产品类型
     * @return
     */
    @Override
    public Integer queryLoanInfoTotalCount(Integer type){
        return loanInfoMapper.selectLoanInfoTotal(type);
    }

    /**
     *
     * @param pageInfo session 中的 分页模型
     * @return
     */
    @Override
    public PageModel queryLoanInfosByTypeAndPage2(PageModel pageInfo)throws Exception {
        Map<String,Object> map = new HashMap<>();
        map.put("start", (pageInfo.getCurrentPage() - 1) * pageInfo.getPageSize());
        map.put("size",pageInfo.getPageSize());
        map.put("type", pageInfo.getPtype());
        pageInfo.setList(loanInfoMapper.selectLoanInfosByTypeAndPage(map));
        return pageInfo;
    }

    /**
     * 逻辑分页，一次将全部数据查出，放入缓存，减轻并发下数据库的压力，后面再根据逻辑算法实现
     * @param pageInfo 分页信息类
     * @return
     */
    @Override
    public PageModel<LoanInfo> queryLoanInfosByTypeAndLogicPage(PageModel<LoanInfo> pageInfo){
        List<LoanInfo> loanList =(List<LoanInfo>) redisTemplate.opsForValue().get("loanList");
        if(loanList==null){
            synchronized (this){
                loanList =(List<LoanInfo>) redisTemplate.opsForValue().get("loanList");
                if(loanList==null){
                    //去数据库查
                    loanList = loanInfoMapper.selectAll();
                    redisTemplate.opsForValue().set("loanList",loanList,20,TimeUnit.SECONDS);
                }
            }
        }
        for(LoanInfo loanInfo:loanList) {
            System.out.println(loanInfo);
        }
        //数据总条数,随便将不是指定类型的数据清除
        Integer totalSize = 0;
        for(LoanInfo loanInfo:loanList){
            if(loanInfo.getProductType()==pageInfo.getPtype()){
                totalSize++;
            }
        }
        pageInfo.setTotalSize(totalSize);

        Iterator<LoanInfo> iterator = loanList.iterator();
        while(iterator.hasNext()){
            LoanInfo next = iterator.next();
            if(next.getProductType() != pageInfo.getPtype()){
                iterator.remove();  //正确
            }
        }
        //总页数
        Double tc = pageInfo.getTotalSize().doubleValue();
        Double num = Math.ceil(tc / pageInfo.getPageSize());//向上取整,除不尽的时候都向上取整
        pageInfo.setTotalPage(Integer.valueOf(num.intValue()));
        // 从第几条数据开始
        Integer firstIndex = (pageInfo.getCurrentPage() - 1) * pageInfo.getPageSize();
        // 到第几条数据结束
        int lastIndex = loanList.size()- firstIndex;
        if(lastIndex>=pageInfo.getPageSize()) {
            lastIndex = pageInfo.getCurrentPage() * pageInfo.getPageSize();
        }
        System.out.println(pageInfo);
        System.out.println(firstIndex);
        System.out.println(lastIndex);
        for(LoanInfo loanInfo:loanList){
            System.out.println(loanInfo);
        }
        pageInfo.setList(loanList.subList(firstIndex, lastIndex));



        return pageInfo;
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer id){
        return loanInfoMapper.selectByPrimaryKey(id);
    }

    /**
     * 优选标年收益率
     * @param type 产品类型
     * @return
     */
    @Override
    public Double queryLoanInfoHistoryRateAvgByType(Integer type){
        Double loanInfoHistoryRateAvgByType = (Double)redisTemplate.opsForValue().get("loanInfoHistoryRateAvgByType");
        if(loanInfoHistoryRateAvgByType==null){
            synchronized (this){
                loanInfoHistoryRateAvgByType = (Double)redisTemplate.opsForValue().get("loanInfoHistoryRateAvgByType");
                if(loanInfoHistoryRateAvgByType==null){
                    loanInfoHistoryRateAvgByType = loanInfoMapper.selectLoanInfoHistoryRateAvgByType(type);
                    redisTemplate.opsForValue().set("loanInfoHistoryRateAvgByType",loanInfoHistoryRateAvgByType,20,TimeUnit.SECONDS);
                }
            }
        }
        System.out.println("-----"+loanInfoHistoryRateAvgByType+"------");
        return loanInfoHistoryRateAvgByType;
    }

}

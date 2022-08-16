package com.bjpowernode.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.money.utils.constant;
import entity.BidInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import service.RedisService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service(interfaceClass = RedisService.class,timeout = 20000,version = "1.0.0")
@Component
public class RedisServiceImpl implements RedisService {

    @Autowired(required = false)
    RedisTemplate redisTemplate;
    /**
     * 存放注册验证码
     * @param phone 对应手机号码
     * @param code 验证码
     */
    @Override
    public void push(String phone,String code) {
        redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
    }

    /**
     * 根据电话号码取出验证码
     * @param phone
     * @return
     */
    public String pop(String phone){
        return (String) redisTemplate.opsForValue().get(phone);
    }



    //用户电话和投资金额放入缓存中
    @Override
    public void zpush(String  phone, Integer bidMoney) {
        redisTemplate.opsForZSet().incrementScore("bidTop", phone, bidMoney);
        // redisTemplate.exp
    }


    //从缓存中获取用户电话和投资总金额
    @Override
    public List<BidInfo> zpop() {
        List<BidInfo> bidTops=new ArrayList<>();
        Set<ZSetOperations.TypedTuple> bidTop = redisTemplate.opsForZSet().reverseRangeWithScores("bidTop", 0, 5);
        Iterator<ZSetOperations.TypedTuple> iterator = bidTop.iterator();
        while(iterator.hasNext()){
            BidInfo bidInfo = new BidInfo();
            ZSetOperations.TypedTuple zt= iterator.next();
            bidInfo.setPhone(zt.getValue().toString());
            bidInfo.setBidMoney( zt.getScore());
            bidTops.add(bidInfo);
        }
        return bidTops;
    }

    @Override
    /*25、多终端：redis维护一个不停自增长的 数字
    //生成不断增长的数字*/
    public Long  incrementNum(){
        return  redisTemplate.opsForValue().increment(constant.AUTO_INCRE_NUMBER, 11);
    }
}

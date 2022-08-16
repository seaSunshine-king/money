package com.bjpowernode.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.money.mapper.FinanceAccountMapper;
import com.bjpowernode.money.mapper.UserMapper;
import entity.FinanceAccount;
import entity.User;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service(interfaceClass = UserService.class,timeout = 20000,version = "1.0.0")
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    FinanceAccountMapper financeAccountMapper;

    @Autowired(required = false)
    RedisTemplate redisTemplate;

    private static  FinanceAccount financeAccount = new FinanceAccount();

    @Override
    public Long queryUserCount() {
        //设置redisTemplate对象的key的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        Long UserCount = (Long) redisTemplate.opsForValue().get("UserCount");
        if(UserCount==null){
            synchronized (this) {
                UserCount = (Long) redisTemplate.opsForValue().get("UserCount");
                if(UserCount==null) {
                    System.out.println("--数据库查询--");
                    UserCount = userMapper.selectUserCount();
                    redisTemplate.opsForValue().set("UserCount", UserCount, 20, TimeUnit.SECONDS);
                }else {
                    System.out.println("--缓存命中--");
                }
            }
        }else {
            System.out.println("--缓存命中--");
        }
         return UserCount;
    }

    @Override
    public int queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }

    /**
     * 注册
     * @param phone 电话号码
     * @param loginPassword 登录密码
     * @return
     */
    public User addUser(String phone, String loginPassword){
        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        user.setAddTime(new Date());
        //添加用户信息
        int count = userMapper.insertSelective(user);
        if(count<1){
            return null;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                financeAccount.setUid(user.getId());
                financeAccount.setAvailableMoney(888.0);
                int i = financeAccountMapper.insertSelective(financeAccount);
            }
        }).start();
        //送大礼
        return user;
    }

    /**
     * 登录
     * @param phone 电话号码
     * @return
     */
    @Override
    public User quiryUserByPhone(String phone){
        new Thread(new Runnable() {
            @Override
            public void run() {
                userMapper.updateLoginTime(phone,new Date());
            }
        }).start();
        return userMapper.selectUserInfoByPhone(phone);
    }


    /**
     * 注册：实名认证
     * @param user
     * @return
     */
   public int addUserInfo(User user){
        return userMapper.updateByPrimaryKeySelective(user);
    }


    /**
     * 头像上传
     * @param fileName 图片名称
     * @return
     */
    @Override
    public int uploadHeader(Integer id ,String fileName){
        return userMapper.updateHeaderImg(id,fileName);
    }

    //根据用户id查询用户电话
    @Override
    public User queryUserInfoById(Integer userId){
        return userMapper.selectByPrimaryKey(userId);
    }
}

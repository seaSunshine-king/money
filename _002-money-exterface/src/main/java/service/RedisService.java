package service;

import entity.BidInfo;

import java.util.List;

public interface RedisService {

    /**
     * 存放注册验证码
     * @param phone 对应手机号码
     * @param code 验证码
     */
    void push(String phone,String code);

    /**
     * 根据电话号码取出验证码
     * @param phone
     * @return
     */
    String pop(String phone);


    /**
     * 用户电话和投资金额放入缓存中
     * @param phone
     * @param bidMoney
     */
    void zpush(String  phone, Integer bidMoney);

    /**
     * 从缓存中获取用户电话和投资总金额
     * @return
     */
    List<BidInfo> zpop();


    /**
     * 缓存中维护一个不断增长的值，用于多终端订单号重复问题
     * @return
     */
    Long  incrementNum();
}

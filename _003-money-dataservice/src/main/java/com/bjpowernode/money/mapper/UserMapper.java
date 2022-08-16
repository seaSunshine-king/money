package com.bjpowernode.money.mapper;

import entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //平台用户数
    Long selectUserCount();
    //注册，查询电话号码是否已被注册
    int selectUserByPhone(String phone);

    /**
     * 登录
     * @param phone 电话号码
     * @return
     */
    User selectUserInfoByPhone(String phone);

    /**
     * 最近登录时间
     * @param date
     * @return
     */
    int updateLoginTime(String phone,Date date);

    /**
     * 头像上传
     * @param fileName
     * @param id
     * @return
     */
    int updateHeaderImg(Integer id ,String fileName);

}
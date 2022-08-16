package com.bjpowernode.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.money.mapper.FinanceAccountMapper;
import entity.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.FinanceService;

import javax.xml.ws.Action;

@Service(interfaceClass = FinanceService.class,timeout = 20000,version = "1.0.0")
@Component
public class FinanceServiceImpl implements FinanceService{

    @Autowired
    FinanceAccountMapper financeAccountMapper;
    /**
     * 根据用户id查询账户信息
     * @param uid 用户id
     * @return
     */
    @Override
    public FinanceAccount queryFinanceAccountByUserId(Integer uid) {
        return financeAccountMapper.selectByUserId(uid);
    }


}

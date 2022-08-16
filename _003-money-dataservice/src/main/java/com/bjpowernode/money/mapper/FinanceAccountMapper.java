package com.bjpowernode.money.mapper;

import entity.FinanceAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    /**
     * 根据用户id查询账户信息
     * @param userId 用户id
     * @return
     */
    FinanceAccount selectByUserId(Integer userId);

    /**
     * 修改账户余额
     * @param userId 用户id
     * @param bidMoney 透资金额
     * @return
     */
    int updateAvailableMoneyByBidMoney(Integer userId,Double bidMoney);


    /**
     * 收益金额返现
     * @return
     */
    int updateAvailableMoneySumIncomeMoney(Integer userId,Double money);

    /**
     * 充值成功，将充值金额加入用户账户
     * @param userId
     * @param RechargeMoney
     * @return
     */
    int updateAvailableMoneyByRechargeMoney(Integer userId , Double RechargeMoney);

}
package service;

import entity.FinanceAccount;

public interface FinanceService {

    /**
     *查询用户账户余额
     */
    FinanceAccount queryFinanceAccountByUserId(Integer uid);


}

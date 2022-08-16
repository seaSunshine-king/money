package com.bjpowernode.money.utils;

public class constant {
    //总交易额
    public static final String BID_MONEY_SUM = "bidMoneySum";
    //用户总人数
    public static final String USER_COUNT = "UserCount";
    //历史年利率平均数
    public static final String LOANINFO_HISTORY_RATE_AVG = "loanInfoHistoryRateAvg";

    /**
     * 投资返回状态码
     * BID_MONEY_EXCEED_LEFT_MONEY 50001 :投资金额超过产品剩余可投金额
     * ACCOUNT_BALANCE_INSUFFICIENT = 50002:账户余额不足
     * UPDATE_LOAN_LEFT_MONEY_ERROR = 50003; 修改产品剩余可投金额失败
     * UPDATE_PRODUCT_STATUS_ERROR = 50004; 修改产品状态失败
     * INSERT_BID_RECORD_ERROR = 50005; 添加投资记录失败
     * BID_SUCCESS = 20000 投资成功
     */
    public static final Integer BID_MONEY_EXCEED_LEFT_MONEY = 50001;

    public static final Integer ACCOUNT_BALANCE_INSUFFICIENT = 50002;

    public static final Integer UPDATE_LOAN_LEFT_MONEY_ERROR = 50003;

    public static final Integer UPDATE_PRODUCT_STATUS_ERROR = 50004;

    public static final Integer INSERT_BID_RECORD_ERROR = 50005;

    public static final Integer BID_SUCCESS = 20000;

    public static final Long AUTO_INCRE_NUMBER = 10000l;


}

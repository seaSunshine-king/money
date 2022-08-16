package com.bjpowernode.money.mapper;

import entity.LoanInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface LoanInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LoanInfo record);

    int insertSelective(LoanInfo record);

    LoanInfo selectByPrimaryKey(Integer id);

    //逻辑分页用，查询所有产品
    List<LoanInfo> selectAll();

    int updateByPrimaryKeySelective(LoanInfo record);

    int updateByPrimaryKey(LoanInfo record);

    // 动力金融网历史年化收益率
    Double selectLoanInfoHistoryRateAvg();

    //根据产品类型和数量 查询产品信息
    List selectLoanInfosByTypeAndNum(Map parasMap);

    //本页的数据集合
    List selectLoanInfosByTypeAndPage(Map map);

    //数据总条数
    int selectLoanInfoTotal(Integer type);

    //优选投资年收益率
    Double selectLoanInfoHistoryRateAvgByType(Integer type);

    /**
     * 减少可投金额
     * @param bidMoney
     * @return
     */
    int updateLeftMoneyByBidMoney(Double bidMoney, Integer loanId);

    /**
     * 修改产品状态 0未满 1满标
     * @param loanId 产品id
     * @param status 产品状态
     * @param date 满标时间
     * @return
     */
    int updateStatus(Integer loanId, Integer status, Date date);

    /**
     * 根据产品状态查询产品信息
     * @return
     */
    List<LoanInfo> selectLoanInfoByStatus(Integer status);

    /**
     * 将指定产品状态修改为2
     * @param status
     * @return
     */
    int updateProductStatus(Integer loanId,Integer status);

    /**
     * 根据产品类型查询产品id
     * @param type
     * @return
     */
    List<Integer> selectLoanIdByType(Integer type);
}

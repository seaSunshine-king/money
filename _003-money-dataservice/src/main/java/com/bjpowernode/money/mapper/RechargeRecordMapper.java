package com.bjpowernode.money.mapper;

import entity.RechargeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);

    /**
     * 充值失败，修改订单状态为2
     * @param rechargeNo
     * @return
     */
    int updateStatusByRechargeFail(String rechargeNo);

    /**
     * 根据订单编号，查询充值记录
     * @param rechargeNo
     * @return
     */
    RechargeRecord selectRechargeRecordByRechargeNo(String rechargeNo);

    /**
     * 充值成功，修改订单转态为1
     * @param rechargeNo
     * @return
     */
    int updateStatusByRechargeSuccess(String rechargeNo,Integer version);

    /**
     * 以实际支付金额为准，修改订单充值金额
     * @param total_amount
     * @return
     */
    int updateRechargeMoney(Double total_amount,String rechargeNo);

    /**
     * 查询用户投资记录
     * @param userId
     * @return
     */
    List<RechargeRecord> selectRechargeRecordByUserId(Integer userId);

    /**
     * 根据订单状态查询订单信息
     * @param status
     * @return
     */
    List<RechargeRecord> selectRechargeRecordByStatus(Integer status);

    /**
     * 查询用户充值总记录数
     * @param userId
     * @return
     */
    int selectRechargeRecordCount(Integer userId);

    /**
     * 充值订单带分页
     * @param map
     * @return
     */
    List<RechargeRecord> selectRechargeRecordByPage(Map map);


}
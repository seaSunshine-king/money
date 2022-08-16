package service;

import com.bjpowernode.money.utils.PageModel;
import entity.RechargeRecord;

import java.util.List;

public interface RechargeService {

    /**
     * 生产订单
     * @param rechargeRecord
     * @return
     */
    int recharge(RechargeRecord rechargeRecord);

    /**
     * 充值失败，修改订单状态为2
     * @param rechargeNo
     * @return
     */
    int rechargeFail(String rechargeNo);

    /**
     * 根据订单编号，查询充值记录
     * @param rechargeNo
     * @return
     */
    RechargeRecord queryRechargeRecordByRechargeNo(String rechargeNo);

    /**
     * 充值成功，修改订单转态为1，将充值金额加入用户账户
     * @param rechargeRecord
     * @return
     */
    boolean rechargeSuccess(RechargeRecord rechargeRecord);

    /**
     * 以实际支付金额为准，修改订单充值金额
     * @param total_amount
     * @return
     */
    int modifyRechargeMoney(Double total_amount,String rechargeNo);

    /**
     * 查询用户最近投资记录
     * @param userId
     * @return
     */
    List<RechargeRecord> queryRecentRechargeRecordByUserId(Integer userId);

    /**
     * 订单处理：查询状态为0的充值订单
     * @return
     */
    List<RechargeRecord> queryRechargeRecordByZero();

    /**
     * 订单长时间未支付：修改状态未支付失败 2
     * @param no
     * @return
     */
    int modifyRechargeStatusFail(String no);

    /**
     * 充值订单带分页
     * @param pageModel
     * @return
     */
    PageModel<RechargeRecord> queryRechargeRecordByPage(PageModel pageModel);

    /**
     * 查询充值总记录数
     * @param userId
     * @return
     */
    int queryRechargeRecordCount(Integer userId);
}

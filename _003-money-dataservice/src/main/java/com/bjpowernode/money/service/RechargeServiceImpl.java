package com.bjpowernode.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.money.mapper.FinanceAccountMapper;
import com.bjpowernode.money.mapper.RechargeRecordMapper;
import com.bjpowernode.money.mapper.UserMapper;
import com.bjpowernode.money.utils.PageModel;
import entity.BidInfo;
import entity.RechargeRecord;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import service.RechargeService;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = RechargeService.class,timeout = 20000,version = "1.0.0")
@Component
public class RechargeServiceImpl implements RechargeService {
    @Autowired
    RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    FinanceAccountMapper financeAccountMapper;

    @Override
    public int recharge(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    /**
     * 充值失败，修改订单状态为2
     * @param rechargeNo
     * @return
     */
    @Override
    public int rechargeFail(String rechargeNo){
        return rechargeRecordMapper.updateStatusByRechargeFail(rechargeNo);
    }


    /**
     * 根据订单编号，查询充值记录
     * @param rechargeNo
     * @return
     */
    @Override
    public RechargeRecord queryRechargeRecordByRechargeNo(String rechargeNo){
        return rechargeRecordMapper.selectRechargeRecordByRechargeNo(rechargeNo);
    }



    /**
     * 充值成功，修改订单转态为1，将充值金额加入用户账户
     * @param rechargeRecord
     * @return
     */
    @Transactional
    public boolean rechargeSuccess(RechargeRecord rechargeRecord){
        System.out.println(rechargeRecord.getVersion());
        //修改订单状态为1
        int num = rechargeRecordMapper.updateStatusByRechargeSuccess(rechargeRecord.getRechargeNo(),rechargeRecord.getVersion());
        if(num!=1){
            //手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        num = financeAccountMapper.updateAvailableMoneyByRechargeMoney(rechargeRecord.getUid(),rechargeRecord.getRechargeMoney());
        if(num!=1){
            //手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }


    /**
     * 以实际支付金额为准，修改订单充值金额
     * @param total_amount
     * @return
     */
    @Override
    public int modifyRechargeMoney(Double total_amount,String rechargeNo){
        return rechargeRecordMapper.updateRechargeMoney(total_amount,rechargeNo);
    }

    /**
     * 查询用户最近投资记录
     * @param userId
     * @return
     */
    @Override
    public List<RechargeRecord> queryRecentRechargeRecordByUserId(Integer userId){
        List<RechargeRecord> recordList = rechargeRecordMapper.selectRechargeRecordByUserId(userId);
        if(!ObjectUtils.allNotNull(recordList)|| recordList.size()==0){
            return null;
        }
        //时间排序
        recordList.sort(new Comparator<RechargeRecord>() {
            @Override
            public int compare(RechargeRecord o1, RechargeRecord o2) {
                return o2.getRechargeTime().compareTo(o1.getRechargeTime());
            }
        });
        if(recordList.size()>5){
            recordList = recordList.subList(0, 5);
        }
        recordList = recordList.subList(0,recordList.size() );
        return recordList;
    }

    //订单处理：查询状态为0的充值订单
    @Override
    public List<RechargeRecord> queryRechargeRecordByZero(){
        return rechargeRecordMapper.selectRechargeRecordByStatus(0);
    }

    //订单长时间未支付：修改状态未支付失败 2
    @Override
    public int modifyRechargeStatusFail(String no){
        return rechargeRecordMapper.updateStatusByRechargeFail(no);
    }

    /**
     * 充值订单带分页
     * @param pageInfo
     * @return
     */
    @Override
    public PageModel<RechargeRecord> queryRechargeRecordByPage(PageModel pageInfo){
        Map<String,Object> map = new HashMap<>();
        //从第几条开始，以及每页显示多少条
        map.put("userId",pageInfo.getUserId());
        map.put("start", (pageInfo.getCurrentPage() - 1) * pageInfo.getPageSize());
        map.put("size", pageInfo.getPageSize());
        List<RechargeRecord> list = rechargeRecordMapper.selectRechargeRecordByPage(map);
        //排序
        list.sort(new Comparator<RechargeRecord>() {
            @Override
            public int compare(RechargeRecord o1, RechargeRecord o2) {
                return o2.getRechargeTime().compareTo(o1.getRechargeTime());
            }
        });
        pageInfo.setList(list);
        return pageInfo;
    }


    /**
     * 查询充值总记录数
     * @param userId
     * @return
     */
    @Override
    public int queryRechargeRecordCount(Integer userId){
        return rechargeRecordMapper.selectRechargeRecordCount(userId);
    }
}

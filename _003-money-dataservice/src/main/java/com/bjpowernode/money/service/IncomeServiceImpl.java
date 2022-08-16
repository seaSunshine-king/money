package com.bjpowernode.money.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.money.mapper.*;
import com.bjpowernode.money.utils.PageModel;
import entity.BidInfo;
import entity.IncomeRecord;
import entity.LoanInfo;
import entity.RechargeRecord;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import service.IncomeService;

import java.math.BigDecimal;
import java.util.*;

/*收益业务实现类*/
@Service(interfaceClass = IncomeService.class,timeout = 20000,version = "1.0.0")
@Component
public class IncomeServiceImpl implements IncomeService {

    @Autowired
    LoanInfoMapper loanInfoMapper;

    @Autowired
    BidInfoMapper bidInfoMapper;

    @Autowired
    IncomeRecordMapper incomeRecordMapper;

    @Autowired
    FinanceAccountMapper financeAccountMapper;

    //定时器：生成收益计划
    @Override
    @Transactional
    public void generatePlan() {

        //1、查询状态为1 的产品==List
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoByStatus(1);
        if(!ObjectUtils.allNotNull(loanInfoList)){
            return;
        }else {
        //2、遍历集合 ，查询该产品的投资记录==》List
        for(LoanInfo loanInfo:loanInfoList){
            List<BidInfo> bidInfoList = bidInfoMapper.selectByLoanId(loanInfo.getId());
            //3、遍历集合，投资记录生成收益计划
            for(BidInfo bidInfo:bidInfoList){
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setIncomeStatus(0);
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setLoanId(bidInfo.getLoanId());
                Date date = null;
                Double income = null;
                if(loanInfo.getProductType()==0){
                    //新手宝，周期单位：天
                    date = DateUtils.addDays(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    income = loanInfo.getRate()/100/365*bidInfo.getBidMoney()*loanInfo.getCycle();
                }else {
                    //其他产品周期单位：月
                    date = DateUtils.addMonths(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    income = loanInfo.getRate()/100/365*bidInfo.getBidMoney()*loanInfo.getCycle()*30;
                }
                incomeRecord.setIncomeDate(date);
                //四舍五入，保留3位小数
                incomeRecord.setIncomeMoney(new BigDecimal(income).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                int count = incomeRecordMapper.insertSelective(incomeRecord);
                if( count!=1){
                    //手动回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
                //产品状态改为2，形成收益
                count = loanInfoMapper.updateProductStatus(loanInfo.getId(), 2);
                if(count!=1) {
                    //手动回滚
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
                }
            }
        }


    }

    /**
     * 收益返现
     */
    @Override
    @Transactional
    public void cashBack() {
        //找到收益报告中状态为0的，代表还为返现
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectByStatus(0);
        if(!ObjectUtils.allNotNull(incomeRecordList)){
            return;
        }else {
        for(IncomeRecord incomeRecord:incomeRecordList){
            //将收益金额加入对应账户
            int count = financeAccountMapper.updateAvailableMoneySumIncomeMoney(incomeRecord.getUid(),incomeRecord.getIncomeMoney());
            if(count!=1){
                //手动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
            //将返现完成的收益记录状态改为1
            int num = incomeRecordMapper.updateStatusCashBack(incomeRecord.getId());
            if(num!=1) {
                //手动回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
            }
        }
    }

    /**
     * 查询用户最近收益记录
     * @param userId
     * @return
     */
    @Override
    public List<IncomeRecord> queryRecentIncomeRecordByUserId(Integer userId){
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordByUserId(userId);
        if(!ObjectUtils.allNotNull(incomeRecordList)|| incomeRecordList.size()==0){
            return null;
        }
        //时间排序
        incomeRecordList.sort(new Comparator<IncomeRecord>() {
            @Override
            public int compare(IncomeRecord o1, IncomeRecord o2) {
                return o2.getIncomeDate().compareTo(o1.getIncomeDate());
            }
        });
        if(incomeRecordList.size()>5){
            incomeRecordList = incomeRecordList.subList(0, 5);
        }
        incomeRecordList = incomeRecordList.subList(0, incomeRecordList.size());
        return incomeRecordList;
    }

    /**
     * 查询用户收益记录总条数
     * @param userId
     * @return
     */
    @Override
    public Integer queryIncomeRecordCountByUserId(Integer userId){
        return incomeRecordMapper.selectIncomeRecordCountByUserId(userId);
    }


    /**
     * 查看全部收益页面，带分页
     * @param pageInfo
     * @return
     */
    public PageModel<IncomeRecord> queryIncomeRecordByPage(PageModel pageInfo){
        Map<String,Object> map = new HashMap<>();
        //从第几条开始，以及每页显示多少条
        map.put("userId",pageInfo.getUserId());
        map.put("start", (pageInfo.getCurrentPage() - 1) * pageInfo.getPageSize());
        map.put("size", pageInfo.getPageSize());
        List<IncomeRecord> list = incomeRecordMapper.selectIncomeRecordByPage(map);
        //排序
        list.sort(new Comparator<IncomeRecord>() {
            @Override
            public int compare(IncomeRecord o1, IncomeRecord o2) {
                return o2.getIncomeDate().compareTo(o1.getIncomeDate());
            }
        });
        pageInfo.setList(list);
        return pageInfo;
    }
}

package com.bjpowernode.money.mapper;

import entity.IncomeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);

    /**
     * 查找收益报告中状态为0未返现的用户
     * @param status 0 还未返现 1 已返现
     * @return
     */
    List<IncomeRecord> selectByStatus(Integer status);

    /**
     * 将返现完成的收益记录状态改为1
     * @param id
     * @return
     */
    int updateStatusCashBack(Integer id);

    /**
     * 查询用户最近收益记录
     * @param userId
     * @return
     */
    List<IncomeRecord> selectIncomeRecordByUserId(Integer userId);

    /**
     * 查询用户收益记录总条数
     * @param userId
     * @return
     */
    Integer selectIncomeRecordCountByUserId(Integer userId);

    /**
     * 查看全部收益页面，带分页
     * @param map
     * @return
     */
    List<IncomeRecord> selectIncomeRecordByPage(Map map);
}
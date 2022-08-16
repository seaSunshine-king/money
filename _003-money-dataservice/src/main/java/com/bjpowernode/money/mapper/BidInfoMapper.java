package com.bjpowernode.money.mapper;

import entity.BidInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Repository //交给spring容器管理
public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    //根据产品id查询投资信息
    List selectBidInfoByLoanId(Map map);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    //累计成交额
    Double selectBidMoneySum();

    /**
     * 根据产品id查询投资记录条数
     * @param id
     * @return
     */
    int selectBidInfoCountByLoanId(Integer id);

    /**
     * 根据产品id查询投资记录
     * @param loanId
     * @return
     */
    List<BidInfo> selectByLoanId(Integer loanId);

    /**
     * 投资排行
     * @return
     */
    List selectByLoanIds(List list);

    /**
     * 根据用户id查询投资记录，带分页
     * @param map 参数集合
     * @return
     */
    List<BidInfo> selectBidInfoByUserIdPage(Map map);

    /**
     * 根据用户id查询投资记录总条数
     * @param userId
     * @return
     */
    Integer selectBidInfoCountByUserId(Integer userId);

    /**
     * 查询用户最近投资记录
     * @param userId
     * @return
     */
    List<BidInfo> selectBidInfoByUserId(Integer userId);
}
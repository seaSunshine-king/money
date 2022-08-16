package service;

import com.bjpowernode.money.utils.PageModel;
import entity.BidInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 投资业务接口
 */
public interface BidInfoService {

    /**
     *  累计成交额
     * @return
     */
    Double queryBidMoneySum();

    /**
     * 根据产品id查询投资信息
     * @param id 产品id
     * @return
     */
   PageModel<BidInfo> queryBidInfoByLoanId(Integer id, Integer currentPage);

    /**
     * 根据产品id 查询投资信息总条数
     * @param id
     * @return
     */
    Integer queryBidInfoCountByLoanId(Integer id);

    /**
     * 透资
     * @param map 参数集合
      * @return
     */
    Integer invest(Map map);

    /**
     * 投资排行
     * @return
     */
    List queryBidInfoBySort(Integer ptype);

    /**
     * 查询用户最近投资记录
     * @param userId
     * @return
     */
    List<BidInfo> queryBidInfoByUserId(Integer userId);

    /**
     * 查询用户投资记录
     * 带物理分页
     * @param pageInfo 分页信息
     * @return
     */
    PageModel<BidInfo> queryBidInfoByUserIdPage(PageModel pageInfo);

    /**
     * 查询指定用户投资总记录条数
     */
    Integer queryBidInfoCount(Integer userId);
}

package service;

import com.bjpowernode.money.utils.PageModel;
import entity.LoanInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 产品业务接口
 */
public interface LoanInfoService {

    /**
     * 动力金融网历史年化收益率
     * @return
     */
    Double queryLoanInfoHistoryRateAvg();

    /**
     * 根据产品类型和数量 查询产品信息
     * @param parasMap
     * @return
     */
    List<LoanInfo> queryLoanInfosByTypeAndNum(Map<String, Object> parasMap);



    /**
     * 物理分页
     * 查询指定页的产品信息
     * @return
     * @throws SQLException
     * 返回数据库中总的记录数
     * @return  返回数据库中总的记录数
     * @throws SQLException
     */
    PageModel<LoanInfo> queryLoanInfosByTypeAndPage(int currentPage,Integer type) throws Exception;

    //根据id查询产品信息，回显到页面
    LoanInfo queryLoanInfoById(Integer id);

    /**
     * 逻辑分页
     * @param pageInfo 分页信息类
     * @return
     */
    PageModel<LoanInfo> queryLoanInfosByTypeAndLogicPage(PageModel<LoanInfo> pageInfo);

    //优选表年收益率
    Double queryLoanInfoHistoryRateAvgByType(Integer type);

    Integer queryLoanInfoTotalCount(Integer type);

    //独立出来的上一页。下一页
    PageModel queryLoanInfosByTypeAndPage2(PageModel pageInfo) throws Exception;

}

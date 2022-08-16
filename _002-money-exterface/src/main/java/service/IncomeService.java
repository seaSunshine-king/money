package service;

import com.bjpowernode.money.utils.PageModel;
import entity.IncomeRecord;

import java.util.List;

public interface IncomeService {

    /**
     * 生成收益报告
     */
    void generatePlan();

    /**
     * 收益返现
     */
    void cashBack();

    /**
     * 查询用户最近收益记录
     * @param userId
     * @return
     */
    List<IncomeRecord> queryRecentIncomeRecordByUserId(Integer userId);

    /**
     * 查询用户收益记录总条数
     * @param userId
     * @return
     */
    Integer queryIncomeRecordCountByUserId(Integer userId);

    /**
     * 查看全部收益页面，带分页
     * @param pageInfo
     * @return
     */
    PageModel<IncomeRecord> queryIncomeRecordByPage(PageModel pageInfo);
}

package entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户收益记录实体类：映射b_income_record表
 * Serializable:序列化接口
 * @param ：id：用户收益记录标识
 * @param ：：uid：用户标识 u_user表主键
 * @param ：loanId：产品标识，b_loan_info表主键
 * @param ：bidId：投标记录标识，b_bid_info表主键
 * @param ：bidMoney：投资金额
 * @param ：incomeDate：收益时间
 * @param ：incomeMoney：收益金额
 * @param ：incomeStatus：收益状态
 */
public class IncomeRecord implements Serializable {
    private String productName;

    private Integer id;

    private Integer uid;

    private Integer loanId;

    private Integer bidId;

    private Double bidMoney;

    private Date incomeDate;

    private Double incomeMoney;

    private Integer incomeStatus;

    public IncomeRecord() {
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public Integer getBidId() {
        return bidId;
    }

    public void setBidId(Integer bidId) {
        this.bidId = bidId;
    }

    public Double getBidMoney() {
        return bidMoney;
    }

    public void setBidMoney(Double bidMoney) {
        this.bidMoney = bidMoney;
    }

    public Date getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(Date incomeDate) {
        this.incomeDate = incomeDate;
    }

    public Double getIncomeMoney() {
        return incomeMoney;
    }

    public void setIncomeMoney(Double incomeMoney) {
        this.incomeMoney = incomeMoney;
    }

    public Integer getIncomeStatus() {
        return incomeStatus;
    }

    public void setIncomeStatus(Integer incomeStatus) {
        this.incomeStatus = incomeStatus;
    }
}
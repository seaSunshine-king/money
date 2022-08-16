package entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户投标信息实体类：
 *      与数据库b_bid_info表映射
 * Serializable:序列化接口
 * @param: id 投标记录标识，主键，唯一标识
 * @param: loanId:产品标识，映射b_loan_info表主键id
 * @param ：uid：用户标识，映射u_user表主键id
 * @param ：bidMoney：投标金额
 * @param ：bidTime：头标时间
 * @param ：bidStatus：投标状态
 */
public class BidInfo implements Serializable {
    private String phone;

    private Integer id;

    private Integer loanId;

    private Integer uid;

    private Double bidMoney;

    private Date bidTime;

    private Integer bidStatus;

    private String productName;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLoanId() {
        return loanId;
    }

    public void setLoanId(Integer loanId) {
        this.loanId = loanId;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Double getBidMoney() {
        return bidMoney;
    }

    public void setBidMoney(Double bidMoney) {
        this.bidMoney = bidMoney;
    }

    public Date getBidTime() {
        return bidTime;
    }

    public void setBidTime(Date bidTime) {
        this.bidTime = bidTime;
    }

    public Integer getBidStatus() {
        return bidStatus;
    }

    public void setBidStatus(Integer bidStatus) {
        this.bidStatus = bidStatus;
    }

    public BidInfo() {
    }

    @Override
    public String toString() {
        return "BidInfo{" +
                "phone='" + phone + '\'' +
                ", id=" + id +
                ", loanId=" + loanId +
                ", uid=" + uid +
                ", bidMoney=" + bidMoney +
                ", bidTime=" + bidTime +
                ", bidStatus=" + bidStatus +
                ", productName='" + productName + '\'' +
                '}';
    }
}
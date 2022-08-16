package entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 投标产品信息实体类：映射b_loan_info表
 * Serializable:序列化接口
 * id:产品标识，主键
 * productName：产品名称
 * rate：产品利率
 * cycle：产品周期
 * releaseTime：产品发布时间
 * productType：产品类型
 * productNo：产品编号
 * productMoney：产品募集资金（产品金额）
 * leftProductMoney：产品剩余可投金额
 * bidMinLimit：起投金额(最小投资金额)
 * bidMaxLimit每笔投资最大金额
 * productStatus：产品状态 产品状态 0未满标，1已满标，2满标已生成收益计划
 * productFullTime：产品满标时间
 * productDesc：产品描述
 * version：版本号
 */
public class LoanInfo implements Serializable {
    private Integer id;

    private String productName;

    private Double rate;

    private Integer cycle;

    private Date releaseTime;

    private Integer productType;

    private String productNo;

    private Double productMoney;

    private Double leftProductMoney;

    private Double bidMinLimit;

    private Double bidMaxLimit;

    private Integer productStatus;

    private Date productFullTime;

    private String productDesc;

    private Integer version;

    public LoanInfo(Integer id){
        this.id = id;
    }

    public LoanInfo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public Integer getProductType() {
        return productType;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo == null ? null : productNo.trim();
    }

    public Double getProductMoney() {
        return productMoney;
    }

    public void setProductMoney(Double productMoney) {
        this.productMoney = productMoney;
    }

    public Double getLeftProductMoney() {
        return leftProductMoney;
    }

    public void setLeftProductMoney(Double leftProductMoney) {
        this.leftProductMoney = leftProductMoney;
    }

    public Double getBidMinLimit() {
        return bidMinLimit;
    }

    public void setBidMinLimit(Double bidMinLimit) {
        this.bidMinLimit = bidMinLimit;
    }

    public Double getBidMaxLimit() {
        return bidMaxLimit;
    }

    public void setBidMaxLimit(Double bidMaxLimit) {
        this.bidMaxLimit = bidMaxLimit;
    }

    public Integer getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(Integer productStatus) {
        this.productStatus = productStatus;
    }

    public Date getProductFullTime() {
        return productFullTime;
    }

    public void setProductFullTime(Date productFullTime) {
        this.productFullTime = productFullTime;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc == null ? null : productDesc.trim();
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "LoanInfo{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", rate=" + rate +
                ", cycle=" + cycle +
                ", releaseTime=" + releaseTime +
                ", productType=" + productType +
                ", productNo='" + productNo + '\'' +
                ", productMoney=" + productMoney +
                ", leftProductMoney=" + leftProductMoney +
                ", bidMinLimit=" + bidMinLimit +
                ", bidMaxLimit=" + bidMaxLimit +
                ", productStatus=" + productStatus +
                ", productFullTime=" + productFullTime +
                ", productDesc='" + productDesc + '\'' +
                ", version=" + version +
                '}';
    }
}
package entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户充值记录实体类：映射b_recharge_record表
 * Serializable:序列化接口
 * id：用户充值记录标识
 * uid：用户标识
 * rechargeNo：充值订单号
 * rechargeStatus：充值订单状态
 * rechargeMoney：充值金额
 * rechargeTime：充值时间
 * rechargeDesc：充值描述
 */
public class RechargeRecord implements Serializable {
    private Integer id;

    private Integer uid;

    private String rechargeNo;

    private String rechargeStatus;

    private Double rechargeMoney;

    private Date rechargeTime;

    private String rechargeDesc;

    //乐观锁，版本号
    private Integer version;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public RechargeRecord() {
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

    public String getRechargeNo() {
        return rechargeNo;
    }

    public void setRechargeNo(String rechargeNo) {
        this.rechargeNo = rechargeNo == null ? null : rechargeNo.trim();
    }

    public String getRechargeStatus() {
        return rechargeStatus;
    }

    public void setRechargeStatus(String rechargeStatus) {
        this.rechargeStatus = rechargeStatus == null ? null : rechargeStatus.trim();
    }

    public Double getRechargeMoney() {
        return rechargeMoney;
    }

    public void setRechargeMoney(Double rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
    }

    public Date getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(Date rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public String getRechargeDesc() {
        return rechargeDesc;
    }

    public void setRechargeDesc(String rechargeDesc) {
        this.rechargeDesc = rechargeDesc == null ? null : rechargeDesc.trim();
    }
}
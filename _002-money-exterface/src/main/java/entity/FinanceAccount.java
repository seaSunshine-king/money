package entity;

import java.io.Serializable;

/**
 * 用户财务资金账户实体类：映射数据库中的u_finance_account表
 * Serializable:序列化接口
 * @param ：id 用户财务资金账户标识，主键，唯一标识
 * @param:uid：用户标识 u_user表主键
 * @param :availableMoney:用户可用资金
 */
public class FinanceAccount implements Serializable {
    private Integer f_id;

    private Integer uid;

    private Double availableMoney;

    public FinanceAccount() {
    }

    public Integer getId() {
        return f_id;
    }

    public void setId(Integer id) {
        this.f_id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Double getAvailableMoney() {
        return availableMoney;
    }

    public void setAvailableMoney(Double availableMoney) {
        this.availableMoney = availableMoney;
    }
}
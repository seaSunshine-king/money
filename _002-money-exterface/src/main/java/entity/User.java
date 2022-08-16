package entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表：映射u_user表
 * Serializable:序列化接口
 * id:用户标识
 * phone：用户电话号码
 * loginPassword：用户登录密码
 * name：用户姓名
 * idCard：用户身份证号码
 * addTime：注册时间
 * lastLoginTime：最近一次登录时间
 * headerImage：用户头像
 */
public class User implements Serializable {
    private Integer id;

    private String phone;

    private String loginPassword;

    private String name;

    private String idCard;

    private Date addTime;

    private Date lastLoginTime;

    private String headerImage;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword == null ? null : loginPassword.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard == null ? null : idCard.trim();
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage == null ? null : headerImage.trim();
    }
}
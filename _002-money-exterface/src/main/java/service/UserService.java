package service;

import entity.User;

/**
 * 用户业务接口
 */
public interface UserService {

    /**
     * 平台用户数
     * @return
     */
    Long queryUserCount();

    /**
     *注册，校验电话号码是否被占用
     * @param phone
     * @return
     */
    int queryUserByPhone(String phone);

    /**
     * 注册
     * @param phone 电话号码
     * @param loginPassword 登录密码
     * @return
     */
    User addUser(String phone, String loginPassword);

    /**
     * 登录
     * @param phone 电话号码
     * @return
     */
    User quiryUserByPhone(String phone);

    /**
     * 注册：实名认证
     * @param user
     * @return
     */
    int addUserInfo(User user);

    /**
     * 头像上传
     * @param fileName 图片名称
     * @return
     */
    int uploadHeader(Integer id ,String fileName);

    /**
     * 根据用户id查询用户电话
     * @param userId
     * @return
     */
    User queryUserInfoById(Integer userId);
}

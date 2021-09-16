package com.xxx.proj.service;

import com.xxx.proj.pojo.TbUser;

/**
 * 用户表服务层接口
 *
 * @author Administrator
 */
public interface UserService {
    /**
     * 发送验证码
     *
     * @param phone
     */
    public void createSmsCode(String phone);

    /**
     * 校验验证码
     *
     * @param phone
     * @param code
     * @return
     */
    public boolean checkSmsCode(String phone, String code);



    /**
     * 增加
     */
    public void add(TbUser user);


}

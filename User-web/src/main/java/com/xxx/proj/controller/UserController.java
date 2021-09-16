package com.xxx.proj.controller;

/**
 * @author admin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021年09月09日 23:08:00
 */

import com.alibaba.dubbo.config.annotation.Reference;
import com.xxx.proj.dto.Result;
import com.xxx.proj.pojo.TbUser;
import com.xxx.proj.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户表controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;


    @RequestMapping("/sendCode")
    public Result sendCode(String phone) {

        try {
            userService.createSmsCode(phone);
            return new Result(true, "验证码发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "验证码发送失败");
        }
    }

    /**
     * 增加
     *
     * @param user
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbUser user, String smscode) {
        boolean checkSmsCode = userService.checkSmsCode(user.getPhone(), smscode);
        if (!checkSmsCode) return new Result(false, "验证码输入错误");
        try {
            userService.add(user);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }


}

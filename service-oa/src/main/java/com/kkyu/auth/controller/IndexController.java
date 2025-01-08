package com.kkyu.auth.controller;

import com.kkyu.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * IndexController
 * 该控制器用于处理与系统管理后台相关的登录、用户信息以及登出操作。
 *
 */
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    /**
     * 用户登录接口
     *
     * @return Result<Map<String, Object>> 返回一个包含登录凭证 (token) 的 Map
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login() {
        // 使用Map存储返回的token信息
        Map<String, Object> map = new HashMap<>();
        map.put("token", "admin-token");
        // 返回成功结果，包含token信息
        return Result.successData(map);
    }

    /**
     * 获取用户信息接口
     *
     * @return Result<Map<String, Object>> 返回一个包含用户角色、名称、头像的Map
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        // 使用Map存储返回的用户信息
        Map<String, Object> map = new HashMap<>();
        map.put("roles", "[admin]");
        map.put("name", "admin");
        map.put("avatar", "https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        // 返回成功结果，包含用户信息
        return Result.successData(map);
    }

    /**
     * 用户退出登录接口
     *
     * @return Result<String> 返回退出成功的消息
     */
    @PostMapping("logout")
    public Result<String> logout() {
        // 返回成功消息，表示用户已退出
        return Result.successMsg("退出登陆成功");
    }

}

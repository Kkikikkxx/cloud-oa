package com.kkyu.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kkyu.auth.service.SysMenuService;
import com.kkyu.auth.service.SysUserService;
import com.kkyu.common.jwt.JwtHelper;
import com.kkyu.common.result.Result;
import com.kkyu.common.utils.MD5;
import com.kkyu.config.exception.DiyException;
import com.kkyu.model.system.SysUser;
import com.kkyu.vo.system.LoginVo;
import com.kkyu.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LoginController
 * 该控制器用于处理与系统管理后台相关的登录、用户信息以及登出操作。
 */
@RestController
@RequestMapping("/admin/system/index")
public class IndexController {

    private final SysUserService sysUserService;
    private final SysMenuService sysMenuService;

    @Autowired
    public IndexController(SysUserService sysUserService, SysMenuService sysMenuService) {
        this.sysUserService = sysUserService;
        this.sysMenuService = sysMenuService;
    }

    /**
     * 用户登录接口
     *
     * @param loginVo 登录请求对象
     * @return Result<Map < String, Object>> 返回一个包含登录凭证 (token) 的 Map
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginVo loginVo) {
        SysUser sysUser = validateUser(loginVo);

        // 生成并返回JWT Token
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("token", token);
        return Result.successData(resultData);
    }

    /**
     * 获取用户信息接口
     *
     * @param request HttpServletRequest 从请求头中解析 Token
     * @return Result<Map < String, Object>> 返回一个包含用户角色、名称、头像、菜单和权限的 Map
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> info(HttpServletRequest request) {
        // 从请求头中获取Token
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()) {
            return Result.error("Token 不存在或无效");
        }

        // 解析 Token 获取用户 ID
        Long userId = JwtHelper.getUserId(token);
        if (userId == null) {
            return Result.error("Token 解析失败，请重新登录");
        }

        // 获取用户信息
        SysUser sysUser = sysUserService.getById(userId);
        if (sysUser == null) {
            return Result.error("用户不存在");
        }

        // 封装用户信息
        Map<String, Object> userInfo = buildUserInfo(sysUser, userId);
        return Result.successData(userInfo);
    }

    /**
     * 用户退出登录接口
     *
     * @return Result<String> 返回退出成功的消息
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.successMsg("退出登录成功");
    }

    // 方法封装

    /**
     * 校验用户登录信息
     *
     * @param loginVo 登录请求对象
     * @return SysUser 验证通过的用户对象
     */
    private SysUser validateUser(LoginVo loginVo) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginVo.getUsername());
        SysUser sysUser = sysUserService.getOne(wrapper);

        if (sysUser == null) {
            throw new DiyException(201, "用户名不存在");
        }

        if (!MD5.encrypt(loginVo.getPassword()).equals(sysUser.getPassword())) {
            throw new DiyException(201, "密码错误");
        }

        if (sysUser.getStatus() == 0) {
            throw new DiyException(201, "用户已禁用");
        }

        return sysUser;
    }

    /**
     * 构建用户信息数据
     *
     * @param sysUser 用户实体
     * @param userId  用户ID
     * @return Map<String, Object> 用户信息数据
     */
    private Map<String, Object> buildUserInfo(SysUser sysUser, Long userId) {
        // 获取用户菜单与权限
        List<RouterVo> routerList = sysMenuService.findUserMenuListByUserId(userId);
        List<String> permsList = sysMenuService.findUserPermsByUserId(userId);

        // 构建用户信息 Map
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("roles", "[admin]");  // 默认角色，建议后续从数据库中获取
        userInfo.put("name", sysUser.getName());
        userInfo.put("avatar", "https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        userInfo.put("routers", routerList);
        userInfo.put("perms", permsList);
        return userInfo;
    }

}

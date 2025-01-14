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
    @PostMapping("login")
    public Result<Map<String,Object>> login(@RequestBody LoginVo loginVo) {
        String username = loginVo.getUsername();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserService.getOne(wrapper);
        if (null == sysUser) {
            throw new DiyException(201, "用户不存在");
        }
        String password_db = sysUser.getPassword();
        String password_input = MD5.encrypt(loginVo.getPassword());
        if (!password_db.equals(password_input)) {
            throw new DiyException(201, "密码错误");
        }
        if (sysUser.getStatus() == 0) {
            throw new DiyException(201, "用户被禁用");
        }

        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        return Result.successData(map);
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

        // 解析 Token 获取用户 ID
        Long userId = JwtHelper.getUserId(token);

        // 获取用户信息
        SysUser sysUser = sysUserService.getById(userId);

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

}

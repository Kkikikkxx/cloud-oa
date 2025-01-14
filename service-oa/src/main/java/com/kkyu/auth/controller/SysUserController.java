package com.kkyu.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kkyu.auth.service.SysUserService;
import com.kkyu.common.result.Result;
import com.kkyu.common.utils.MD5;
import com.kkyu.model.system.SysUser;
import com.kkyu.vo.system.SysUserQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * 该类提供了用于管理系统用户的各种接口，包括用户的增、删、改、查以及状态更新。
 */
@RestController
@RequestMapping("/admin/system/sysUser")  // 统一前缀 /admin/system/sysUser
public class SysUserController {

    // 用户服务接口
    private final SysUserService sysUserService;

    /**
     * 构造函数注入用户服务类
     *
     * @param userService 用户服务
     */
    @Autowired
    public SysUserController(SysUserService userService) {
        this.sysUserService = userService;
    }

    /**
     * 分页查询用户列表
     *
     * @param page           页码，从1开始
     * @param limit          每页记录数
     * @param sysUserQueryVo 查询条件封装对象，包括用户名关键字、创建时间范围等
     * @return 返回分页的用户列表数据封装在Result对象中
     */
    @GetMapping("{page}/{limit}")
    public Result<Page<SysUser>> index(
            @PathVariable Long page,
            @PathVariable Long limit,
            SysUserQueryVo sysUserQueryVo) {

        // 创建分页对象
        Page<SysUser> pageParam = new Page<>(page, limit);

        // 创建查询条件构造器
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();

        // 提取查询条件参数
        String username = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();

        // 根据条件动态生成SQL过滤器
        if (!StringUtils.isEmpty(username)) {
            wrapper.like("username", username);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time", createTimeEnd);
        }

        // 执行分页查询
        Page<SysUser> userPage = sysUserService.page(pageParam, wrapper);

        // 返回查询结果
        return Result.successData(userPage);
    }

    /**
     * 根据用户ID获取用户详细信息
     *
     * @param id 用户ID
     * @return 返回用户详情封装在Result对象中
     */
    @GetMapping("get/{id}")
    public Result<SysUser> get(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        return Result.successData(user);
    }

    /**
     * 新增用户信息
     *
     * @param user 用户实体信息
     * @return 返回新增结果
     */
    @PostMapping("save")
    public Result<String> save(@RequestBody SysUser user) {
        // 加密密码
        String password = user.getPassword();
        user.setPassword(MD5.encrypt(password));
        sysUserService.save(user);
        return Result.successMsg("新增用户成功");
    }

    /**
     * 根据用户ID更新用户信息
     *
     * @param user 用户实体，包含需要更新的字段
     * @return 返回更新结果
     */
    @PutMapping("update")
    public Result<String> updateById(@RequestBody SysUser user) {
        sysUserService.updateById(user);
        return Result.successMsg("修改用户成功");
    }

    /**
     * 根据用户ID删除用户
     *
     * @param id 用户ID
     * @return 返回删除结果
     */
    @DeleteMapping("remove/{id}")
    public Result<String> remove(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.successMsg("删除用户成功");
    }

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 用户状态（1：启用，0：禁用）
     * @return 返回状态更新结果
     */
    @GetMapping("updateStatus/{id}/{status}")
    public Result<String> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        sysUserService.updateStatus(id, status);
        return Result.successMsg("修改用户状态成功");
    }

}

package com.kkyu.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kkyu.auth.service.SysUserService;
import com.kkyu.common.result.Result;
import com.kkyu.model.system.SysUser;
import com.kkyu.vo.system.SysUserQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/admin/system/susUser")
public class SysUserController {

    private final SysUserService userService;

    @Autowired
    public SysUserController(SysUserService userService) {
        this.userService = userService;
    }

    /**
     * 分页查询用户列表
     *
     * @param page           页码
     * @param limit          每页数量
     * @param sysUserQueryVo 查询条件封装对象
     * @return 分页用户数据
     */
    @GetMapping("{page}/{limit}")
    public Result<Page<SysUser>> index(
            @PathVariable Long page,
            @PathVariable Long limit,
            SysUserQueryVo sysUserQueryVo
    ) {
        Page<SysUser> pageParam = new Page<>(page, limit);
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        String username = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();

        if (!StringUtils.isEmpty(username)) {
            wrapper.like("username", username);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time", createTimeEnd);
        }
        Page<SysUser> userPage = userService.page(pageParam, wrapper);

        return Result.successData(userPage);
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("get/{id}")
    public Result<SysUser> get(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        return Result.successData(user);
    }

    /**
     * 新增用户
     *
     * @param user 用户实体
     * @return 新增结果
     */
    @PostMapping("save")
    public Result<String> save(@RequestBody SysUser user) {
        userService.save(user);
        return Result.successMsg("新增用户成功");
    }

    /**
     * 修改用户
     *
     * @param user 用户实体
     * @return 修改结果
     */
    @PutMapping("update")
    public Result<String> updateById(@RequestBody SysUser user) {
        userService.updateById(user);
        return Result.successMsg("修改用户成功");
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("remove/{id}")
    public Result<String> remove(@PathVariable Long id) {
        userService.removeById(id);
        return Result.successMsg("删除用户成功");
    }

    /**
     * 更新用户状态
     *
     * @param id     用户ID
     * @param status 用户状态
     * @return 更新状态结果
     */
    @GetMapping("updateStatus/{id}/{status}")
    public Result<String> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        userService.updateStatus(id, status);
        return Result.successMsg("修改用户状态成功");
    }

}

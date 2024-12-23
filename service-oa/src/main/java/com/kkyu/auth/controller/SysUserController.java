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
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author kkyu
 * @since 2024-12-23
 */
@RestController
@RequestMapping("/admin/system/susUser")
public class SysUserController {

    private final SysUserService userService;

    @Autowired
    public SysUserController(SysUserService userService) {
        this.userService = userService;
    }

    @GetMapping("{page}/{limit}")
    public Result<Page<SysUser>> index(@PathVariable Long page,
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

    @GetMapping("get/{id}")
    public Result<SysUser> get(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        return Result.successData(user);
    }

    @PostMapping("save")
    public Result<String> save(@RequestBody SysUser user) {
        userService.save(user);
        return Result.successMsg("新增用户成功");
    }

    @PutMapping("update")
    public Result<String> updateById(@RequestBody SysUser user) {
        userService.updateById(user);
        return Result.successMsg("修改用户成功");
    }

    @DeleteMapping("remove/{id}")
    public Result<String> remove(@PathVariable Long id) {
        userService.removeById(id);
        return Result.successMsg("删除用户成功");
    }

}


package com.kkyu.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kkyu.auth.service.SysRoleService;
import com.kkyu.common.result.Result;
import com.kkyu.model.system.SysRole;
import com.kkyu.vo.system.SysRoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色管理接口
 * 提供角色相关的操作，如查询所有角色等
 */
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    // 注入 Service
    private final SysRoleService sysRoleService;

    @Autowired
    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @GetMapping("/findAll")
    public Result<List<SysRole>> findAll() {
        List<SysRole> list = sysRoleService.list();

        return Result.successData(list);
    }

    /**
     * 条件分页查询
     */
    @GetMapping("{page}/{limit}")
    public Result<Page<SysRole>> pageQueryRole(
            @PathVariable Long page,
            @PathVariable Long limit,
            SysRoleQueryVo sysRoleQueryVo
    ) {

        Page<SysRole> pageParams = new Page<>(page, limit);
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if (!StringUtils.isEmpty(roleName)){
            // 封装
            wrapper.like("role_name",roleName);
        }

        Page<SysRole> pageData = sysRoleService.page(pageParams, wrapper);

        return Result.successData(pageData);
    }

}

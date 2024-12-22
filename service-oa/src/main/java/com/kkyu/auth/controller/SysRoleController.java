package com.kkyu.auth.controller;

import com.kkyu.auth.service.SysRoleService;
import com.kkyu.common.result.Result;
import com.kkyu.model.system.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    // 注入 Service
    private final SysRoleService sysRoleService;

    @Autowired
    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    // 查询所有的角色
    @GetMapping("/findAll")
    public Result<List<SysRole>> findAll() {
        List<SysRole> list = sysRoleService.list();

        return Result.successData(list);
    }

}

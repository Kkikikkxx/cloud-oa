package com.kkyu.auth.controller;

import com.kkyu.auth.service.SysMenuService;
import com.kkyu.auth.service.impl.SysMenuServiceImpl;
import com.kkyu.common.result.Result;
import com.kkyu.model.system.SysMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器，用于管理菜单的增删改查操作。
 * </p>
 *
 */
@RestController
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 构造器注入SysMenuService实现类
     *
     * @param sysMenuService 菜单服务实现类
     */
    @Autowired
    public SysMenuController(SysMenuServiceImpl sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    /**
     * 获取菜单列表数据，以树形结构展示
     *
     * @return 返回树形结构的菜单列表
     */
    @GetMapping("findNodes")
    public Result<List<SysMenu>> findNodes() {
        List<SysMenu> list = sysMenuService.findNodes();
        return Result.successData(list);
    }

    /**
     * 新增菜单
     *
     * @param sysMenu 菜单实体类，包含菜单信息
     * @return 返回新增成功的信息
     */
    @PostMapping("save")
    public Result<String> save(@RequestBody SysMenu sysMenu) {
        sysMenuService.save(sysMenu);
        return Result.successMsg("新增菜单成功");
    }

    /**
     * 更新菜单
     *
     * @param sysMenu 菜单实体类，包含更新后的菜单信息
     * @return 返回更新成功的信息
     */
    @PutMapping("update")
    public Result<String> updateById(@RequestBody SysMenu sysMenu) {
        sysMenuService.updateById(sysMenu);
        return Result.successMsg("更新菜单成功");
    }

    /**
     * 根据ID删除菜单
     *
     * @param id 菜单ID
     * @return 返回删除成功的信息
     */
    @DeleteMapping("remove/{id}")
    public Result<String> remove(@PathVariable Long id) {
        sysMenuService.removeMenuById(id);
        return Result.successMsg("删除菜单成功");
    }

}

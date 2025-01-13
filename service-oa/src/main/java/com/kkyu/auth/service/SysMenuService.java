package com.kkyu.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkyu.model.system.SysMenu;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取菜单列表数据，以树形结构展示
     *
     * @return 返回树形结构的菜单列表
     */
    List<SysMenu> findNodes();

    /**
     * 根据ID删除菜单
     *
     * @param id 菜单ID
     */
    void removeMenuById(Long id);
}

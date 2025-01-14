package com.kkyu.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkyu.model.system.SysMenu;
import com.kkyu.vo.system.AssginMenuVo;
import com.kkyu.vo.system.RouterVo;

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

    /**
     * 根据角色ID获取菜单列表数据，以树形结构展示
     *
     * @param roleId 角色ID
     * @return 返回角色对应的菜单列表数据，以树形结构展示
     */
    List<SysMenu> findMenuByRoleId(Long roleId);

    /**
     * 分配角色菜单
     *
     * @param assginRoleVo 分配角色菜单的参数对象，包含角色ID和菜单ID列表
     */
    void doAssign(AssginMenuVo assginRoleVo);

    /**
     * 根据用户ID获取菜单路由列表数据
     *
     * @param userId 用户ID
     * @return 返回用户对应的菜单路由列表数据
     */
    List<RouterVo> findUserMenuListByUserId(Long userId);

    /**
     * 根据用户id查询权限
     * @param userId 用户id
     * @return 权限列表
     */
    List<String> findUserPermsByUserId(Long userId);
}

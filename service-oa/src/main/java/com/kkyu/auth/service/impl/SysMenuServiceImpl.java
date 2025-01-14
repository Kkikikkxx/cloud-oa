package com.kkyu.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kkyu.auth.mapper.SysMenuMapper;
import com.kkyu.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kkyu.auth.service.SysRoleMenuService;
import com.kkyu.config.exception.DiyException;
import com.kkyu.model.system.SysMenu;
import com.kkyu.model.system.SysRoleMenu;
import com.kkyu.vo.system.AssginMenuVo;
import com.kkyu.vo.system.MetaVo;
import com.kkyu.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kkyu.auth.utils.MenuHelper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code @class} SysMenuServiceImpl
 * {@code @description} 系统菜单服务实现类，提供了菜单的增删改查以及角色分配相关的业务逻辑
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    /**
     * 角色与菜单的中间表服务，用于管理角色与菜单的关联关系
     */
    private final SysRoleMenuService sysRoleMenuService;

    /**
     * 使用构造器注入SysRoleMenuService
     *
     * @param sysRoleMenuService 角色菜单服务
     */
    @Autowired
    public SysMenuServiceImpl(SysRoleMenuService sysRoleMenuService) {
        this.sysRoleMenuService = sysRoleMenuService;
    }

    /**
     * 获取所有菜单数据，并以树形结构返回
     *
     * @return 返回树形结构的菜单列表
     */
    @Override
    public List<SysMenu> findNodes() {
        // 查询所有菜单数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);

        // 使用工具类构建树形结构
        return MenuHelper.buildTree(sysMenuList);
    }

    /**
     * 根据菜单ID删除菜单
     * 如果该菜单下存在子菜单，则不允许删除
     *
     * @param id 菜单ID
     * @throws DiyException 如果存在子菜单则抛出异常
     */
    @Override
    public void removeMenuById(Long id) {
        // 查询该菜单下是否存在子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        Integer count = baseMapper.selectCount(wrapper);

        // 如果存在子菜单，则抛出异常
        if (count > 0) {
            throw new DiyException(201, "菜单有子菜单，不能删除");
        }

        // 如果没有子菜单，执行删除操作
        baseMapper.deleteById(id);
    }

    /**
     * 根据角色ID获取菜单列表，并以树形结构展示
     *
     * @param roleId 角色ID
     * @return 返回树形结构的菜单列表
     */
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        // 查询所有状态为启用的菜单
        LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus, 1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapperSysMenu);

        // 根据角色ID查询角色与菜单的关联关系
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(wrapperSysRoleMenu);

        // 提取该角色分配的菜单ID
        List<Long> menuIDList = sysRoleMenuList.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());

        // 标记该角色已分配的菜单
        allSysMenuList.forEach(item -> item.setSelect(menuIDList.contains(item.getId())));

        // 构建并返回树形结构的菜单
        return MenuHelper.buildTree(allSysMenuList);
    }

    /**
     * 分配角色菜单权限
     *
     * @param assginRoleVo 分配角色菜单的参数对象，包含角色ID和菜单ID列表
     */
    @Override
    public void doAssign(AssginMenuVo assginRoleVo) {
        // 根据角色ID删除原有的角色菜单分配记录
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, assginRoleVo.getRoleId());
        sysRoleMenuService.remove(wrapper);

        // 遍历传入的菜单ID列表，逐个分配菜单权限
        List<Long> menuIdList = assginRoleVo.getMenuIdList();
        for (Long menuId : menuIdList) {
            // 如果菜单ID为空，则跳过
            if (StringUtils.isEmpty(menuId)) continue;

            // 创建新的角色菜单关联对象
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(assginRoleVo.getRoleId());
            sysRoleMenu.setMenuId(menuId);

            // 保存角色与菜单的关联关系
            sysRoleMenuService.save(sysRoleMenu);
        }
    }

    /**
     * 根据用户ID获取菜单路由列表数据
     *
     * @param userId 用户ID
     * @return 返回用户对应的菜单路由列表数据
     */
    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList;
        // 判断用户是否是管理员 userId = 1
        if (userId == 1) {
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);

        return this.buildRouter(sysMenuTreeList);
    }

    private List<RouterVo> buildRouter(List<SysMenu> menuRouter) {
        List<RouterVo> routerVoList = new ArrayList<>();
        for (SysMenu sysMenu : menuRouter) {
            RouterVo routerVo = new RouterVo();
            routerVo.setHidden(false);
            routerVo.setAlwaysShow(false);
            routerVo.setPath(getRouterPath(sysMenu));
            routerVo.setComponent(sysMenu.getComponent());
            routerVo.setMeta(new MetaVo(sysMenu.getName(), sysMenu.getIcon()));
            List<SysMenu> children = sysMenu.getChildren();
            if (sysMenu.getType() == 1) {
                List<SysMenu> hiddenMenuList = children.stream().filter(item -> !StringUtils.isEmpty(item.getComponent()))
                        .collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routerVoList.add(hiddenRouter);
                }
            } else {
                if (!CollectionUtils.isEmpty(children)) {
                    routerVo.setAlwaysShow(true);
                    routerVo.setChildren(buildRouter(children));
                }
            }
            routerVoList.add(routerVo);
        }
        return routerVoList;
    }

    /**
     * 根据用户id查询用户可以操作的按钮权限
     *
     * @param userId 用户id
     * @return 权限列表
     */
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        //超级管理员admin账号id为：1
        List<SysMenu> sysMenuList;
        // 判断用户是否是管理员 userId = 1
        if (userId == 1) {
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }
        return sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if (menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }
}

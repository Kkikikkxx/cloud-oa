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
 * 系统菜单服务实现类
 * <p>
 * 主要功能：
 * - 提供系统菜单的增删改查功能
 * - 角色与菜单的分配与管理
 * - 菜单的路由构建和用户权限管理
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    /**
     * 角色与菜单中间表服务，用于处理角色与菜单的关联关系
     */
    private final SysRoleMenuService sysRoleMenuService;

    /**
     * 构造器注入角色菜单服务
     *
     * @param sysRoleMenuService 角色菜单服务
     */
    @Autowired
    public SysMenuServiceImpl(SysRoleMenuService sysRoleMenuService) {
        this.sysRoleMenuService = sysRoleMenuService;
    }

    /**
     * 查询所有菜单，并以树形结构返回
     *
     * @return 树形结构的菜单列表
     */
    @Override
    public List<SysMenu> findNodes() {
        // 获取所有菜单数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);

        // 构建树形结构
        return MenuHelper.buildTree(sysMenuList);
    }

    /**
     * 根据菜单ID删除菜单
     * <p>
     * 删除限制：如果菜单存在子菜单，不允许删除
     *
     * @param id 菜单ID
     * @throws DiyException 如果菜单存在子菜单，抛出自定义异常
     */
    @Override
    public void removeMenuById(Long id) {
        // 检查菜单是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        Integer count = baseMapper.selectCount(wrapper);

        // 如果有子菜单，则抛出异常
        if (count > 0) {
            throw new DiyException(201, "菜单有子菜单，不能删除");
        }

        // 删除菜单
        baseMapper.deleteById(id);
    }

    /**
     * 根据角色ID获取菜单列表，并以树形结构返回
     * <p>
     * 包含已分配菜单的标记
     *
     * @param roleId 角色ID
     * @return 树形结构的菜单列表
     */
    @Override
    public List<SysMenu> findMenuByRoleId(Long roleId) {
        // 查询所有启用状态的菜单
        LambdaQueryWrapper<SysMenu> wrapperSysMenu = new LambdaQueryWrapper<>();
        wrapperSysMenu.eq(SysMenu::getStatus, 1);
        List<SysMenu> allSysMenuList = baseMapper.selectList(wrapperSysMenu);

        // 查询角色已分配的菜单ID
        LambdaQueryWrapper<SysRoleMenu> wrapperSysRoleMenu = new LambdaQueryWrapper<>();
        wrapperSysRoleMenu.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuService.list(wrapperSysRoleMenu);
        List<Long> menuIDList = sysRoleMenuList.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());

        // 标记已分配的菜单
        allSysMenuList.forEach(item -> item.setSelect(menuIDList.contains(item.getId())));

        // 构建并返回树形结构
        return MenuHelper.buildTree(allSysMenuList);
    }

    /**
     * 分配角色菜单权限
     *
     * @param assginRoleVo 分配菜单的参数对象
     */
    @Override
    public void doAssign(AssginMenuVo assginRoleVo) {
        // 删除原有分配的角色菜单
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, assginRoleVo.getRoleId());
        sysRoleMenuService.remove(wrapper);

        // 遍历并分配新的菜单权限
        List<Long> menuIdList = assginRoleVo.getMenuIdList();
        for (Long menuId : menuIdList) {
            // 跳过空菜单ID
            if (StringUtils.isEmpty(menuId)) continue;

            // 创建角色菜单关联对象并保存
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(assginRoleVo.getRoleId());
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenuService.save(sysRoleMenu);
        }
    }

    /**
     * 根据用户ID获取菜单路由列表
     * <p>
     * 管理员用户可以查看所有菜单，普通用户只能查看分配的菜单
     *
     * @param userId 用户ID
     * @return 菜单路由列表
     */
    @Override
    public List<RouterVo> findUserMenuListByUserId(Long userId) {
        List<SysMenu> sysMenuList;
        if (userId == 1) {
            // 查询所有启用状态的菜单
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            wrapper.orderByAsc(SysMenu::getSortValue);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            // 查询用户对应的菜单
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

        // 构建树形结构
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTree(sysMenuList);

        // 构建路由列表
        return this.buildRouter(sysMenuTreeList);
    }

    /**
     * 构建路由列表
     *
     * @param menuRouter 菜单数据
     * @return 路由列表
     */
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

            // 如果菜单类型为目录且有子菜单，则递归构建子路由
            if (sysMenu.getType() == 1) {
                List<SysMenu> hiddenMenuList = children.stream().filter(item -> !StringUtils.isEmpty(item.getComponent()))
                        .collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routerVoList.add(hiddenRouter);
                }
            } else if (!CollectionUtils.isEmpty(children)) {
                routerVo.setAlwaysShow(true);
                routerVo.setChildren(buildRouter(children));
            }
            routerVoList.add(routerVo);
        }
        return routerVoList;
    }

    /**
     * 根据用户ID查询用户的按钮权限
     *
     * @param userId 用户ID
     * @return 按钮权限列表
     */
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        List<SysMenu> sysMenuList;
        if (userId == 1) {
            // 查询所有启用状态的菜单
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus, 1);
            sysMenuList = baseMapper.selectList(wrapper);
        } else {
            // 查询用户对应的菜单
            sysMenuList = baseMapper.findMenuListByUserId(userId);
        }

        // 提取按钮权限并返回
        return sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());
    }

    /**
     * 获取路由路径
     *
     * @param menu 菜单对象
     * @return 路由路径
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if (menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }
}

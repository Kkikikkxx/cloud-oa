package com.kkyu.auth.utils;

import com.kkyu.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具类：用于构建菜单树形结构的工具类
 * 提供了使用递归方式创建树形菜单的静态方法
 *
 * @author kkyu
 * @since 2025-01-10
 */
public class MenuHelper {

    /**
     * 使用递归方式构建菜单树
     *
     * @param sysMenuList 包含所有菜单的列表
     * @return 构建好的树形菜单列表
     */
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {
        // 创建一个用于存储最终结果的集合
        List<SysMenu> trees = new ArrayList<>();
        // 遍历菜单列表，将所有顶级菜单（parentId为0）作为入口点
        for (SysMenu sysMenu : sysMenuList) {
            // 如果是顶级菜单（parentId为0），则递归获取其子菜单
            if (sysMenu.getParentId() == 0) {
                trees.add(getChildren(sysMenu, sysMenuList));
            }
        }
        return trees;
    }

    /**
     * 递归获取子菜单
     *
     * @param sysMenu 当前菜单对象
     * @param sysMenuList 包含所有菜单的列表
     * @return 返回包含子菜单的当前菜单对象
     */
    public static SysMenu getChildren(SysMenu sysMenu, List<SysMenu> sysMenuList) {
        // 初始化子菜单列表
        sysMenu.setChildren(new ArrayList<>());

        // 遍历所有菜单，查找当前菜单的子菜单
        for (SysMenu item : sysMenuList) {
            // 如果当前菜单的ID等于某个菜单的父ID，则说明该菜单是当前菜单的子菜单
            if (sysMenu.getId().equals(item.getParentId())) {
                // 如果子菜单列表为空，则初始化
                if (sysMenu.getChildren() == null) {
                    sysMenu.setChildren(new ArrayList<>());
                }
                // 递归查找子菜单
                sysMenu.getChildren().add(getChildren(item, sysMenuList));
            }
        }
        return sysMenu;
    }
}

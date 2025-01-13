package com.kkyu.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kkyu.auth.mapper.SysMenuMapper;
import com.kkyu.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kkyu.config.exception.DiyException;
import com.kkyu.model.system.SysMenu;
import org.springframework.stereotype.Service;
import com.kkyu.auth.utils.MenuHelper;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    /**
     * 获取菜单列表数据，以树形结构展示
     *
     * @return 返回树形结构的菜单列表
     */
    @Override
    public List<SysMenu> findNodes() {
        // 查询所有菜单数据
        List<SysMenu> sysMenuList = baseMapper.selectList(null);

        // 构建树形结构
        return MenuHelper.buildTree(sysMenuList);
    }

    /**
     * 根据ID删除菜单
     *
     * @param id 菜单ID
     */
    @Override
    public void removeMenuById(Long id) {
        // 判断当前菜单是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new DiyException(201, "菜单有子菜单，不能删除");
        }
        baseMapper.deleteById(id);
    }
}

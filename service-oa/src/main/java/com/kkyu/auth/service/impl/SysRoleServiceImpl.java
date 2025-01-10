package com.kkyu.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kkyu.auth.mapper.SysRoleMapper;
import com.kkyu.auth.mapper.SysUserRoleMapper;
import com.kkyu.auth.service.SysRoleService;
import com.kkyu.model.system.SysRole;
import com.kkyu.model.system.SysUserRole;
import com.kkyu.vo.system.AssginRoleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 角色管理服务实现类
 * 该类继承 MyBatis-Plus 提供的 ServiceImpl，用于实现 SysRoleService 接口中的方法。
 * 提供了角色的查询与分配功能。
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    /**
     * 用户角色映射表的 Mapper，用于进行用户与角色的数据库交互
     */
    private final SysUserRoleMapper sysUserRoleMapper;

    /**
     * 使用构造函数注入方式，实现 SysUserRoleMapper 的依赖注入。
     * @param sysUserRoleMapper 用户角色关联表 Mapper
     */
    @Autowired
    public SysRoleServiceImpl(SysUserRoleMapper sysUserRoleMapper) {
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    /**
     * 根据用户ID查询角色数据
     * 该方法通过用户ID获取该用户拥有的角色以及所有可用的角色列表。
     *
     * @param userId 用户的唯一标识符
     * @return 返回一个包含两个列表的Map对象：
     *         - "assginRoleList"：用户已分配的角色列表
     *         - "allRolesList"：所有角色列表
     */
    @Override
    public Map<String, Object> findRoleByAdminId(Long userId) {
        // 查询所有的角色，返回一个完整的角色列表
        List<SysRole> allRolesList = baseMapper.selectList(null);

        // 查询用户已分配的角色ID列表
        List<SysUserRole> existUserRoleList = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
                        .select(SysUserRole::getRoleId)
        );

        // 提取出用户已经分配的角色ID集合
        List<Long> existRoleIdList = existUserRoleList.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        // 根据用户已有角色ID进行角色分类
        List<SysRole> assginRoleList = new ArrayList<>();
        for (SysRole role : allRolesList) {
            // 如果角色ID存在于已分配的角色ID集合中，添加到已分配角色列表中
            if (existRoleIdList.contains(role.getId())) {
                assginRoleList.add(role);
            }
        }

        // 构建返回的 Map 集合
        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assginRoleList", assginRoleList);
        roleMap.put("allRolesList", allRolesList);
        return roleMap;
    }

    /**
     * 为用户分配角色
     * 该方法会根据传入的用户ID与角色ID列表，将用户的角色进行重新分配。
     * 事务管理：确保在整个过程中，若有异常会进行回滚，保持数据一致性。
     *
     * @param assginRoleVo 分配角色的对象，包含用户ID与角色ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doAssign(AssginRoleVo assginRoleVo) {
        // 删除用户原有的角色分配关系
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, assginRoleVo.getUserId()));

        // 遍历传入的角色ID列表，逐个进行分配
        for (Long roleId : assginRoleVo.getRoleIdList()) {
            // 如果角色ID为空，则跳过本次循环
            if (StringUtils.isEmpty(roleId)) continue;

            // 创建用户角色关联对象，并设置用户ID与角色ID
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(assginRoleVo.getUserId());
            userRole.setRoleId(roleId);

            // 将新的用户角色关系插入数据库
            sysUserRoleMapper.insert(userRole);
        }
    }
}

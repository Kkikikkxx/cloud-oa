package com.kkyu.auth.service.impl;

import com.kkyu.auth.mapper.SysUserMapper;
import com.kkyu.auth.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kkyu.model.system.SysUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SysUserServiceImpl
 * 用户表服务实现类，继承自 MyBatis-Plus 提供的 ServiceImpl，用于实现 SysUserService 接口。
 * 提供对用户数据的具体业务操作方法。
 * 主要功能：
 * - 更新用户状态
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /**
     * 更新用户状态
     * 根据用户ID和新的状态更新用户状态。
     * 该方法使用了事务管理，确保数据一致性。
     *
     * @param id     用户的唯一标识符 (主键ID)
     * @param status 用户的新状态 (如启用/禁用等)
     * @throws IllegalArgumentException 如果用户不存在
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        // 根据用户ID从数据库中查询用户
        SysUser sysUser = baseMapper.selectById(id);

        // 检查用户是否存在
        if (sysUser == null) {
            throw new IllegalArgumentException("用户不存在，无法更新状态");
        }

        // 更新用户状态并保存到数据库
        sysUser.setStatus(status);
        baseMapper.updateById(sysUser);
    }
}

package com.kkyu.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkyu.model.system.SysUser;

public interface SysUserService extends IService<SysUser> {

    /**
     * 更新用户状态
     * @param id 用户id
     * @param status 状态
     */
    void updateStatus(Long id, Integer status);
}

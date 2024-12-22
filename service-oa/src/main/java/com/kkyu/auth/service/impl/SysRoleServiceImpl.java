package com.kkyu.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kkyu.auth.mapper.SysRoleMapper;
import com.kkyu.auth.service.SysRoleService;
import com.kkyu.model.system.SysRole;
import org.springframework.stereotype.Service;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

}

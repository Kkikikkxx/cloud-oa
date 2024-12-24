package com.kkyu.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkyu.model.system.SysRole;
import com.kkyu.vo.system.AssginRoleVo;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {

    /**
     * 根据用户id查询角色数据
     *
     * @param userId 用户id
     * @return 返回一个包含角色数据的Map对象，其中键是角色的标识，值是角色的详细信息
     */
    Map<String, Object> findRoleByAdminId(Long userId);

    /**
     * 分配角色给用户
     * 此方法用于将角色分配给特定用户，实现角色与用户的关联
     *
     * @param assginRoleVo 包含用户ID和角色ID等信息的实体类，用于指定分配的角色和用户
     */
    void doAssign(AssginRoleVo assginRoleVo);

}

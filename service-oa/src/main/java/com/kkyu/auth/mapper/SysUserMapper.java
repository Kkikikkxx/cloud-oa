package com.kkyu.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kkyu.model.system.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author kkyu
 * @since 2024-12-23
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}

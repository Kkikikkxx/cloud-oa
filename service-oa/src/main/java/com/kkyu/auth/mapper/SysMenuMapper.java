package com.kkyu.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kkyu.model.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author kkyu
 * @since 2025-01-10
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> findMenuListByUserId(@Param("userId") Long userId);
}

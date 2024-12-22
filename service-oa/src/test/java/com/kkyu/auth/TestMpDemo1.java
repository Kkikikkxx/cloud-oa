package com.kkyu.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kkyu.auth.mapper.SysRoleMapper;
import com.kkyu.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TestMpDemo1 {

    // 注入
    private final SysRoleMapper sysRoleMapper;

    @Autowired
    public TestMpDemo1(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    // 查询
    @Test
    public void getAll() {
        List<SysRole> list = sysRoleMapper.selectList(null);

        System.out.println(list);
    }

    // 新增
    @Test
    public void add() {
        SysRole sysRole = new SysRole();

        sysRole.setRoleName("角色2");
        sysRole.setRoleCode("role2");
        sysRole.setDescription("角色描述2");

        int rows = sysRoleMapper.insert(sysRole);

        System.out.println(rows);
        System.out.println(sysRole.getId());
    }

    //修改操作
    @Test
    public void update() {
        //根据id查询
        SysRole role = sysRoleMapper.selectById(10);
        //设置修改值
        role.setRoleName("atguigu角色管理员");
        //调用方法实现最终修改
        int rows = sysRoleMapper.updateById(role);
        System.out.println(rows);
    }

    //删除操作
    @Test
    public void deleteId() {
        int rows = sysRoleMapper.deleteById(10);
    }

    //批量删除
    @Test
    public void testDeleteBatchIds() {
        int result = sysRoleMapper.deleteBatchIds(Arrays.asList(1, 2));
        System.out.println(result);
    }

    //条件查询
    @Test
    public void testQuery1() {
        //创建QueryWrapper对象，调用方法封装条件
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_name","普通管理员");
        //调用mp方法实现查询操作
        List<SysRole> list = sysRoleMapper.selectList(wrapper);
        System.out.println(list);
    }

    @Test
    public void testQuery2() {
        //LambdaQueryWrapper，调用方法封装条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName,"普通管理员");
        //调用mp方法实现查询操作
        List<SysRole> list = sysRoleMapper.selectList(wrapper);
        System.out.println(list);
    }
}

package com.kkyu.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kkyu.auth.service.SysRoleService;
import com.kkyu.common.result.Result;
import com.kkyu.model.system.SysRole;
import com.kkyu.vo.system.AssginRoleVo;
import com.kkyu.vo.system.SysRoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色管理接口
 * 提供角色相关的操作，如查询所有角色等
 */
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {

    // 注入 Service
    private final SysRoleService sysRoleService;

    @Autowired
    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @GetMapping("/toAssign/{userId}")
    public Result<Map<String, Object>> toAssign(@PathVariable Long userId) {
        Map<String, Object> roleMap = sysRoleService.findRoleByAdminId(userId);
        return Result.successData(roleMap);
    }

    /**
     * 分配角色
     *
     * @param assginRoleVo 分配角色的请求参数
     * @return 分配角色的结果
     */
    @PostMapping("/doAssign")
    public Result<String> doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssign(assginRoleVo);
        return Result.successMsg("分配角色成功");
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @GetMapping("/findAll")
    public Result<List<SysRole>> findAll() {
        List<SysRole> list = sysRoleService.list();

        /*// 模拟异常
        try {
            int i = 10/0;
        } catch (Exception e){
            throw new DiyException(20001, "自定义异常");
        }*/

        return Result.successData(list);
    }

    /**
     * 条件分页查询
     *
     * @param page           分页参数，指定页码
     * @param limit          分页参数，指定每页记录数
     * @param sysRoleQueryVo 查询条件封装对象
     * @return 分页查询结果
     */
    @GetMapping("{page}/{limit}")
    public Result<Page<SysRole>> pageQueryRole(
            @PathVariable Long page,
            @PathVariable Long limit,
            SysRoleQueryVo sysRoleQueryVo
    ) {

        Page<SysRole> pageParams = new Page<>(page, limit);
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if (!StringUtils.isEmpty(roleName)) {
            // 封装
            wrapper.like("role_name", roleName);
        }

        Page<SysRole> pageData = sysRoleService.page(pageParams, wrapper);

        return Result.successData(pageData);
    }

    /**
     * 添加角色
     *
     * @param role 要添加的角色信息
     * @return 添加结果
     */
    @PostMapping("save")
    public Result<String> addRole(@RequestBody SysRole role) {
        return sysRoleService.save(role) ? Result.successMsg("添加成功") : Result.error("添加失败");
    }

    /**
     * 根据ID查询角色信息。
     *
     * @param id 角色的唯一标识符
     * @return 包含查询结果的Result对象，其中data字段为SysRole对象
     */
    @GetMapping("get/{id}")
    public Result<SysRole> get(@PathVariable Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        return Result.successData(sysRole);
    }

    /**
     * 修改指定的角色信息。
     *
     * @param role 包含更新信息的角色对象
     * @return 包含操作结果信息的Result对象
     */
    @PutMapping("update")
    public Result<String> updateById(@RequestBody SysRole role) {
        return sysRoleService.updateById(role) ? Result.successMsg("修改成功") : Result.error("修改失败");
    }

    /**
     * 根据ID删除角色。
     *
     * @param id 要删除的角色的唯一标识符
     * @return 包含操作结果信息的Result对象
     */
    @DeleteMapping("remove/{id}")
    public Result<String> remove(@PathVariable Long id) {
        return sysRoleService.removeById(id) ? Result.successMsg("删除成功") : Result.error("删除失败");
    }

    /**
     * 批量删除多个角色。
     *
     * @param idList 包含要删除的角色ID列表
     * @return 包含操作结果信息的Result对象
     */
    @DeleteMapping("batchRemove")
    public Result<String> batchRemove(@RequestBody List<Long> idList) {
        return sysRoleService.removeByIds(idList) ? Result.successMsg("删除成功") : Result.error("删除失败");
    }

}

package com.walker.service;

import com.walker.common.util.Page;
import com.walker.mode.Role;
import com.walker.mode.RoleUser;

import java.util.List;

public interface RoleService {


    List<Role> saveAll(List<Role> obj);
    Integer[] deleteAll(List<String> ids);

    Role get(Role obj);
    Integer delete(Role obj);

    List<Role> finds(Role obj, Page page);
    Integer count(Role obj);



    /**
     * 查询角色
     * @param id 传入user id则查user的role 传入dept id则查dept的role
     * @param sFlag 是否包含未拥有的
     * @return
     */
    List<Role> getRoles(String id, String sFlag);

    /**
     * 保存角色关联
     * @param id 用户/部门
     * @param onRoleIds 开启的角色id列表
     * @param offRoleIds 关闭的角色id列表
     * @return
     */
    List<RoleUser> saveRoles(String id, List<String> onRoleIds, List<String> offRoleIds);


    /**
     * 查询角色的用户列表
     * @param obj
     * @param page
     * @return
     */
    List<RoleUser> finds(RoleUser obj, Page page);

    Integer count(RoleUser obj);

}

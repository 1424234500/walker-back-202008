package com.walker.service;

import com.walker.common.util.Page;
import com.walker.mode.Role;

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

}

package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.config.Config;
import com.walker.dao.JdbcDao;
import com.walker.dao.RoleRepository;
import com.walker.mode.Role;
import com.walker.service.RoleService;
import com.walker.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;


    @Override
    public List<Role> saveAll(List<Role> objs) {
        return roleRepository.saveAll(objs);
    }

    @Override
    public Integer delete(Role obj) {
        roleRepository.deleteById(obj.getID());
        return 1;
    }

    @Override
    public Role get(Role obj) {
        Optional<Role> result = roleRepository.findById(obj.getID());
        return result.isPresent()?result.get():null;
    }

    @Override
    public List<Role> finds(Role obj, Page page) {
        Pageable pageable = Config.turnTo(page);
        org.springframework.data.domain.Page<Role> res = roleRepository.findAll(this.getSpecification(obj), pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public Integer count(Role obj) {
        long res = roleRepository.count(this.getSpecification(obj));
        return new Long(res).intValue();
    }
    private Specification getSpecification(Role obj){
        return new Specification<Role>(){
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotEmpty(obj.getID())) {
                    list.add(criteriaBuilder.like(root.get("ID"), "%" + obj.getID() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getS_MTIME())) {
                    list.add(criteriaBuilder.greaterThan(root.get("S_MTIME"), obj.getS_MTIME()));
                }
                if (StringUtils.isNotEmpty(obj.getS_ATIME())) {
                    list.add(criteriaBuilder.greaterThan(root.get("S_ATIME"), obj.getS_ATIME()));
                }
                if (StringUtils.isNotEmpty(obj.getS_FLAG())) {
                    list.add(criteriaBuilder.equal(root.get("S_FLAG"), obj.getS_FLAG()));
                }



                if (StringUtils.isNotEmpty(obj.getNAME())) {
                    list.add(criteriaBuilder.like(root.get("NAME"), "%" + obj.getNAME() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getNUM())) {
                    list.add(criteriaBuilder.greaterThan(root.get("NUM"), obj.getNUM()));
                }
                if (StringUtils.isNotEmpty(obj.getLEVEL())) {
                    list.add(criteriaBuilder.equal(root.get("LEVEL"), obj.getLEVEL()));
                }

                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };
    }

    @Override
    public Integer[] deleteAll(List<String> ids) {
        int res = roleRepository.selfDeleteAll(ids);
        return new Integer[]{res};
    }


    /**
     * 迫于 连表时 jpa异常和  sharding导致的第二张表名小写问题 原生sql实现
     */
    @Autowired
    JdbcDao jdbcDao;

    /**
     * 获取关联表role role_user的赋予用户的权限
     *
     * @param id
     * @param sFlag 是否包含未拥有的
     * @return
     */
    @Override
    public List<Role> getRoles(String id, String sFlag) {
//        return roleRepository.getRoles(id, sFlag);
        List<Map<String, Object>> list = jdbcDao.find("select * from ( " +
                "select r.ID,r.LEVEL,r.NAME,r.NUM,r.s_ATIME,r.s_MTIME,ifnull(ru.S_FLAG,'0') S_FLAG " +
                "from W_ROLE r " +
                "left join W_ROLE_USER ru " +
                "on ru.USER_ID=? and r.ID=ru.ROLE_ID " +
                " ) t " +
                "where S_FLAG like concat('%', ?, '%') ", id, sFlag);


        return SpringContextUtil.mapToBean(list, new Role());
    }




}






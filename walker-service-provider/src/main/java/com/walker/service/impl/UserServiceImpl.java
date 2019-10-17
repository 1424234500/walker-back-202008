package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.config.Config;
import com.walker.dao.UserRepository;
import com.walker.dao.UserRepository;
import com.walker.mode.User;
import com.walker.service.UserService;
import com.walker.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public List<User> saveAll(List<User> objs) {
        return userRepository.saveAll(objs);
    }

    @Override
    public Integer delete(User obj) {
        userRepository.deleteById(obj.getID());
        return 1;
    }

    @Override
    public User get(User obj) {
        Optional<User> result = userRepository.findById(obj.getID());
        return result.isPresent()?result.get():null;
    }

    @Override
    public List<User> finds(User obj, Page page) {
        Pageable pageable = Config.turnTo(page);
        org.springframework.data.domain.Page<User> res = userRepository.findAll(this.getSpecification(obj), pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public Integer count(User obj) {
        long res = userRepository.count(this.getSpecification(obj));
        return new Long(res).intValue();
    }
    private Specification getSpecification(User obj){
        return new Specification<User>(){
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

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
                if (StringUtils.isNotEmpty(obj.getSEX())) {
                    list.add(criteriaBuilder.equal(root.get("SEX"), obj.getSEX()));
                }
                if (StringUtils.isNotEmpty(obj.getNAME())) {
                    list.add(criteriaBuilder.like(root.get("NAME"), "%" + obj.getNAME() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getNICK_NAME())) {
                    list.add(criteriaBuilder.like(root.get("NICK_NAME"), "%" + obj.getNICK_NAME() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getSIGN())) {
                    list.add(criteriaBuilder.like(root.get("SIGN"), "%" + obj.getSIGN() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getEMAIL())) {
                    list.add(criteriaBuilder.like(root.get("EMAIL"), "%" + obj.getEMAIL() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getMOBILE())) {
                    list.add(criteriaBuilder.like(root.get("MOBILE"), "%" + obj.getMOBILE() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getDEPT_CODE())) {
                    list.add(criteriaBuilder.like(root.get("DEPT_CODE"), "%" + obj.getDEPT_CODE() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getPWD())) {
                    list.add(criteriaBuilder.like(root.get("PWD"), "%" + obj.getPWD() + "%"));
                }
                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };
    }

    @Override
    public Integer[] deleteAll(List<String> ids) {
        int res = userRepository.selfDeleteAll(ids);
        return new Integer[]{res};
    }
}

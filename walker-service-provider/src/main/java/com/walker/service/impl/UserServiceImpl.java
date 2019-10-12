package com.walker.service.impl;

import com.walker.common.util.Page;
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
        userRepository.deleteById(obj.getId());
        return 1;
    }

    @Override
    public User get(User obj) {
        return userRepository.selfFindOneCacheJPQL(obj.getId());
    }

    @Override
    public List<User> finds(User obj, Page page) {
        String order = page.getOrder();
        String[] orders = order.split(" ");

        Sort sort = orders[0].length() > 0
                ? new Sort(orders.length > 1 && orders[1].equalsIgnoreCase("DESC")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC, orders[0])
                : null;

        //jpa分页从0开始
        Pageable pageable =
                sort == null
                        ? PageRequest.of(page.getNowpage()-1, page.getShownum())
                        : PageRequest.of(page.getNowpage()-1, page.getShownum(), sort);



        org.springframework.data.domain.Page<User> res = userRepository.findAll(new Specification<User>(){
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> list = new ArrayList<>();

                Path<String> namepath = root.get("name");
                Path<String> sexpath =root.get("id");
                EntityType<User> u = root.getModel();
                if (StringUtils.isNotEmpty(obj.getName())) {
                    list.add(criteriaBuilder.like(root.get("name"), "%" + obj.getName() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getId())) {
                    list.add(criteriaBuilder.like(root.get("id"), "%" + obj.getId() + "%"));
                }
                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        }, pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public Integer count(User obj) {
        return ((Long)(userRepository.count(new Specification<User>(){
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> list = new ArrayList<>();

                Path<String> namepath = root.get("name");
                Path<String> sexpath =root.get("id");
                EntityType<User> u = root.getModel();
                if (StringUtils.isNotEmpty(obj.getName())) {
                    list.add(criteriaBuilder.like(root.get("name"), "%" + obj.getName() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getId())) {
                    list.add(criteriaBuilder.like(root.get("id"), "%" + obj.getId() + "%"));
                }
                return criteriaBuilder.and(list.toArray(new Predicate[0]));

            }
        }))).intValue();
    }

    @Override
    public Integer[] deleteAll(List<String> ids) {
        int res = userRepository.selfDeleteAll(ids);
        return new Integer[]{res};
    }
}

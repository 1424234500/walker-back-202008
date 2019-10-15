package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.dao.TeacherRepository;
import com.walker.mode.Teacher;
import com.walker.service.TeacherService;
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
import java.util.List;

@Service("teacherService")
public class TeacherJpaServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;


    @Override
    public List<Teacher> saveAll(List<Teacher> objs) {
        return teacherRepository.saveAll(objs);
    }

    @Override
    public Integer delete(Teacher obj) {
        teacherRepository.deleteById(obj.getID());
        return 1;
    }

    @Override
    public Teacher get(Teacher obj) {
        return teacherRepository.getOne(obj.getID());
    }

    @Override
    public List<Teacher> finds(Teacher obj, Page page) {
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


        org.springframework.data.domain.Page<Teacher> res = teacherRepository.findAll(this.getSpecification(obj), pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public Integer count(Teacher obj) {
        return ((Long)(teacherRepository.count(this.getSpecification(obj)))).intValue();
    }

    @Override
    public Integer[] deleteAll(List<String> ids) {
        int res = teacherRepository.selfDeleteAll(ids);
        return new Integer[]{res};
    }



    private Specification getSpecification(Teacher obj){
        return new Specification<Teacher>(){
            @Override
            public Predicate toPredicate(Root<Teacher> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotEmpty(obj.getNAME())) {
                    list.add(criteriaBuilder.like(root.get("NAME"), "%" + obj.getNAME() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getID())) {
                    list.add(criteriaBuilder.like(root.get("ID"), "%" + obj.getID() + "%"));
                }
                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };
    }
}

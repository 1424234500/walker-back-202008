package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.dao.StudentRepository;
import com.walker.mode.Student;
import com.walker.service.StudentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service("studentService")
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;


    @Override
    public List<Student> saveAll(List<Student> objs) {
        return studentRepository.saveAll(objs);
    }

    @Override
    public Integer delete(Student obj) {
        studentRepository.deleteById(obj.getID());
        return 1;
    }

    @Override
    public Student get(Student obj) {
        return studentRepository.selfFindOneCacheJPQL(obj.getID());
    }

    @Override
    public List<Student> finds(Student obj, Page page) {
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

        org.springframework.data.domain.Page<Student> res = studentRepository.findAll(this.getSpecification(obj), pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public Integer count(Student obj) {
        long res = studentRepository.count(this.getSpecification(obj));
        return new Long(res).intValue();
    }
    private Specification getSpecification(Student obj){
        return new Specification<Student>(){
            @Override
            public Predicate toPredicate(Root<Student> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotEmpty(obj.getID())) {
                    list.add(criteriaBuilder.like(root.get("ID"), obj.getID() + "%"));
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

                if (StringUtils.isNotEmpty(obj.getCLASS_CODE())) {
                    list.add(criteriaBuilder.like(root.get("CLASS_CODE"), "%" + obj.getCLASS_CODE() + "%"));
                }
                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };
    }

    @Override
    public Integer[] deleteAll(List<String> ids) {
        int res = studentRepository.selfDeleteAll(ids);
        return new Integer[]{res};
    }
}

package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.dao.TeacherRepository;
import com.walker.mode.Teacher;
import com.walker.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("teacherJpaService")
public class TeacherJpaServiceImpl implements TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;


    @Override
    public Teacher add(Teacher test) {
        return teacherRepository.save(test);
    }

    @Override
    public Integer update(Teacher test) {
        return teacherRepository.selfUpdateCacheJPQL(test.getName(), test.getId());
    }

    @Override
    public Integer delete(Teacher test) {
        teacherRepository.deleteById(test.getId());
        return 1;
    }

    @Override
    public Teacher get(Teacher test) {
        return teacherRepository.selfFindOneCacheJPQL(test.getId());
    }

    @Override
    public List<Teacher> finds(Teacher test, Page page) {
        Sort sort = new Sort(Sort.Direction.ASC, "name");
        Pageable pageable = new PageRequest(page.getNowpage()-1, page.getShownum(), sort);
        page.setNum(teacherRepository.selfCount(test.getName()));
        return teacherRepository.selfFindPage(test.getName(), pageable);
    }

    @Override
    public Integer count(Teacher test) {
        return Integer.valueOf(Long.valueOf(teacherRepository.count()).intValue());
    }
}
package com.walker.dao;

import com.walker.ApplicationTests;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.mode.Teacher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TeacherRepositoryTest extends ApplicationTests {
    @Autowired
    TeacherRepository teacherRepository;
    @Test
    public void test(){
        Teacher obj = teacherRepository.save(new Teacher().setId("1").setName("hello").setTime(TimeUtil.getTimeYmdHmss()));
        Tools.out("save", obj);
        List<Teacher> mans = new ArrayList<>();
        for(int i = 0; i < 32; i++){
            mans.add(new Teacher().setId("id_" + i).setName("hello" + i  ).setTime(TimeUtil.getTimeYmdHmss()));
        }
        Tools.out("saveAll", teacherRepository.saveAll(mans));
        Pageable pageable = PageRequest.of(0, 33);
        Page<Teacher> pageRes = teacherRepository.findAll(pageable);
        Tools.out(pageRes.getTotalElements());
        Tools.formatOut(pageRes.getContent());
        Tools.out(teacherRepository.findById("1"));

    }

}
package com.walker.service.impl;

import com.walker.ApplicationProviderTests;
import com.walker.common.util.Page;
import com.walker.common.util.TimeUtil;
import com.walker.mode.Teacher;
import com.walker.service.TeacherService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TeacherJpaServiceImplTest extends ApplicationProviderTests {
    @Autowired
    @Qualifier("teacherJpaService")
    TeacherService teacherJpaService;
    @Autowired
    @Qualifier("teacherJdbcService")
    TeacherService teacherJdbcService;

    @Test
    public void findsJpa() {
        Page page = null;
        page = new Page().setNowpage(1).setShownum(2);
        out(teacherJpaService.finds(new Teacher().setName(""), page));
        out(page);

        out(teacherJpaService.finds(new Teacher().setName("1"), page));
        out(page);

        out(teacherJpaService.finds(new Teacher().setName("add"), page));
        out(page);

        out(teacherJpaService.finds(new Teacher().setName("test"), page));
        out(page);
    }
    @Test
    public void findsJdbc() {
        Page page = new Page().setNowpage(1).setShownum(2);
        out(teacherJdbcService.finds(new Teacher().setName(""), page));
        out(page);

        out(teacherJdbcService.finds(new Teacher().setName("1"), page));
        out(page);

        out(teacherJdbcService.finds(new Teacher().setName("add"), page));
        out(page);

        out(teacherJdbcService.finds(new Teacher().setName("test"), page));
        out(page);
    }

    @Test
    public void deleteAll(){
        out(teacherJpaService.deleteAll(Arrays.asList(new String[]{"id_2,id_5"})));
        out(teacherJdbcService.deleteAll(Arrays.asList(new String[]{"3,33"})));
    }



    @Test
    public void add(){
        List<Teacher> list = new ArrayList<>();
        for(int i = 0; i < 100; i++ ) {
            String seri = TimeUtil.getTime("SSS");
            list.add(new Teacher()
                    .setId("id_" + seri)
                    .setName("name_" + seri)
                    .setPwd("pwd_" + seri)
                    .setTime("" + seri)

            );
        }
        out(list.size(), teacherJpaService.saveAll(list));


    }

}
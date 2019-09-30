package com.walker.service.impl;

import com.walker.ApplicationProviderTests;
import com.walker.common.util.Page;
import com.walker.mode.Teacher;
import com.walker.service.MessageService;
import com.walker.service.TeacherService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.*;

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
}
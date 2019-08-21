package com.walker.config;

import com.walker.common.util.Tools;
import com.walker.common.util.Watch;
import com.walker.dao.TeacherRepository;
import com.walker.mode.Teacher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheConfigTest {
    @Autowired
    TeacherRepository teacherRepository;


    @Test
    public void cacheManager() {
        StopWatch sw = new StopWatch();
        sw.start("save");
        Teacher teacher = teacherRepository.save(new Teacher().setId("1").setName("name").setPwd("pwd").setTime("time"));
        Tools.out(teacher);
        sw.stop();
        sw.start("findOneCache1");
        Teacher teacher1 = teacherRepository.selfFindOneCacheJPQL("1");
        Tools.out(teacher1);
        sw.stop();

        sw.start("findOneCache2");
        Teacher teacher2 = teacherRepository.selfFindOneCacheJPQL("1");
        Tools.out(teacher2);
        sw.stop();

        sw.start("findOneCache3");
        Teacher teacher3 = teacherRepository.selfFindOneCacheJPQL("1");
        Tools.out(teacher3);
        sw.stop();

        sw.start("updateCache");
        int res = teacherRepository.selfUpdateCacheJPQL("name-new", "1");
        Tools.out("update", res);
        sw.stop();


        sw.start("findOneCache4");
        Teacher teacher4 = teacherRepository.selfFindOneCacheJPQL("1");
        Tools.out(teacher4);
        sw.stop();


        Tools.out(sw.prettyPrint());




    }

    @Test
    public void keyGenerator() {

    }
}
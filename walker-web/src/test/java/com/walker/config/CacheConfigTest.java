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

import javax.tools.Tool;
import java.util.Arrays;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheConfigTest {
    @Autowired
    TeacherRepository teacherRepository;


    @Test
    public void cacheManager() {
        StopWatch sw = new StopWatch();
        for(int i = 0; i < 5; i++){
            sw.start("save" + i);
            Tools.out("save" + i, teacherRepository.save(new Teacher().setId(i+"").setName("name" + i).setPwd("pwd").setTime("time")));
            sw.stop();
        }
        for(int i = 0; i < 3; i++) {
            sw.start("findOneCache" + i);
            Tools.out("findcache" + i, teacherRepository.selfFindOneCacheJPQL("1"));
            sw.stop();
        }
        for(int i = 0; i < 3; i++) {
            sw.start("findListCache" + i);
            Tools.out("findList", teacherRepository.selfFindListCacheJPQL(Arrays.asList("1", "2", "3")));
            sw.stop();
        }
        sw.start("findListCache1");
        Tools.out("findList", teacherRepository.selfFindListCacheJPQL(Arrays.asList("1", "2")));

        sw.stop();
        sw.start("updateCache");
        int res = teacherRepository.selfUpdateCacheJPQL("name-new", "1");
        Tools.out("update", res);
        sw.stop();


        sw.start("findOneCache4");
        Teacher teacher4 = teacherRepository.selfFindOneCacheJPQL("1");
        Tools.out("findOne new", teacher4);
        sw.stop();
        sw.start("findListCache2");
        Tools.out("findList new", teacherRepository.selfFindListCacheJPQL(Arrays.asList("1", "2")));
        sw.stop();

        Tools.out(sw.prettyPrint());




    }

    @Test
    public void keyGenerator() {

    }
}
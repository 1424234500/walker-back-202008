package com.walker.service.impl;

import com.walker.ApplicationProviderTests;
import com.walker.common.util.Page;
import com.walker.common.util.TimeUtil;
import com.walker.mode.User;
import com.walker.service.TeacherService;
import com.walker.service.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class UserServiceImplTest extends ApplicationProviderTests {
    @Autowired
    UserService userService;

    @Test
    public void save() {
        List<User> list = new ArrayList<>();
        for(int i = 0; i < 100; i++ ) {
            String seri = TimeUtil.getTime("SSS");
            list.add(new User()
                    .setId("id_" + seri)
                    .setName("name_" + seri)
                    .setPwd("pwd_" + seri)
                    .setDeptCode("" + seri)
                    .setEmail("" + seri + "@walker.com")
                    .setMobile("18408249" + seri)
                    .setNickName("nick_name_" + seri)
                    .setSign("sign_" + seri)
                    .setsAtime(TimeUtil.getTimeYmdHmss())
                    .setsMtime(TimeUtil.getTimeYmdHmss())
                    .setsFlag("1")
            );
        }
        out(list.size(), userService.saveAll(list));
    }

    @Test
    public void finds() {
        Page page = new Page().setShownum(4);
        List<User> list = userService.finds(new User()
                .setId("id_1")
                .setName("name_1")
        , page);
        out(list);
        out(page);

    }

    @Test
    public void deleteAll() {
        out(userService.get(new User().setId("id_364")));

    }
}
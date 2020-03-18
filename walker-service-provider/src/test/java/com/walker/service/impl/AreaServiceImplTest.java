package com.walker.service.impl;

import com.walker.ApplicationProviderTests;
import com.walker.common.util.Page;
import com.walker.dao.AreaRepository;
import com.walker.mode.Area;
import com.walker.service.AreaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.*;

public class AreaServiceImplTest extends ApplicationProviderTests {

    @Autowired
    AreaService areaService;
    @Test
    public void saveAll() {

        areaService.saveAll(Arrays.asList(new Area().setNAME("name").setID("id1"), new Area().setID("id2"), new Area().setID("id3")));

    }

    @Test
    public void finds() {
        out(areaService.finds(new Area().setNAME("name"), new Page().setNum(3)));
    }

    @Test
    public void count() {
        out(areaService.count(new Area()));
    }

    @Test
    public void deleteAll() {
    }
}
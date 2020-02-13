package com.walker.dao;

import com.walker.ApplicationProviderTests;
import com.walker.common.util.Tools;
import com.walker.mode.Area;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class AreaRepositoryTest extends ApplicationProviderTests {

    @Autowired
    AreaRepository areaRepository;

    @Test
    public void findAllByPATH() {
        Area area = new Area().setNAME("name").setID("id").setPATH("ppp");
        areaRepository.save(area);
        out(areaRepository.findAllByID(new HashSet<>(Arrays.asList(area.getID()))));



    }
}
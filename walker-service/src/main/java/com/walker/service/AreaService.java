package com.walker.service;

import com.walker.common.util.Page;
import com.walker.mode.Area;

import java.util.List;

public interface AreaService {


    List<Area> saveAll(List<Area> obj);
    Integer[] deleteAll(List<String> ids);

    Area get(Area obj);
    Integer delete(Area obj);

    List<Area> finds(Area obj, Page page);
    Integer count(Area obj);


    List<Area> findsRoot(Page page);



}

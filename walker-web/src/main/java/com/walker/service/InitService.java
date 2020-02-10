package com.walker.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.walker.common.util.*;
import com.walker.config.Config;
import com.walker.config.MakeConfig;
import com.walker.mode.Area;
import com.walker.mode.Dept;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 初始化数据服务
 */
@Service("initService")
public class InitService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    ScheduleService scheduleService;

    @Autowired
    MakeConfig makeConfig;
    @Autowired
    DeptService deptService;
    @Autowired
    AreaService areaService;

    /**
     * 启动后挂载初始化
     */
    public void initOnStart(){
        //异步初始化
        ThreadUtil.execute(new Runnable() {
           @Override
           public void run() {
//               updateAreaMeituan();
           }
        });
        //异步初始化
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                updateArea();

            }
        });

        //同步初始化
        initQuartz();

    }
    public void initQuartz(){
        log.info("start quartz ");
        try {
            scheduleService.start();
//            scheduleService.add(new Task("com.walker.job.JobTest","sb quartz scheduler tools out", "0 0/2 * * * ?", "0 0/3 * * * ?"));
//            scheduleService.add(new Task("com.walker.job.JobTest2","sb quartz scheduler tools out2", "0 0/5 * * * ?", "0 0/30 * * * ?"));
//            scheduleService.add(new Task("com.walker.job.JobUpdateArea","update area from meituan", "0 0 1 * * ?"));
            log.info("start quartz ok");
        }catch (Exception e){
            log.error("start quartz error", e);
        }
    }

    public void updateAreaMeituan(){

//		https://www.meituan.com/ptapi/getprovincecityinfo/
//[{"provinceCode":"370000","provinceName":"山东","cityInfoList":[{"id":60,"name":"青岛","pinyin":"qingdao","acronym":"qd","rank":"B","firstChar":"Q"}
        log.info("update area start");
        List<Dept> depts = new ArrayList<>();
        List<Dept> deptsChild = new ArrayList<>();
        try {
            String str = new HttpBuilder(makeConfig.urlAreaMeituan, HttpBuilder.Type.GET)
                    .setConnectTimeout(3000).setRequestTimeout(3000).setSocketTimeout(5000)
                    .setEncode("utf-8").setDecode("utf-8").buildString();

            List<Bean> list = JSON.parseObject(str, new TypeReference<List<Bean>>(){});

/*
[{
				"provinceCode":"370000",
				"provinceName":"山东",
				"cityInfoList": [{
					"id":60,
					"name":"青岛",
					"pinyin":"qingdao",
					"acronym":"qd",
					"rank":"B",
					"firstChar":"Q"
				}]
			},
]
*/
            for(Bean bean : list){
                String id = "D" + bean.get("provinceCode", "");
                Dept dept = new Dept().setID(id)
                        .setNAME(bean.get("provinceName", ""))
                        ;
                depts.add(dept);
                JSONArray jsonArray = bean.get("cityInfoList", new JSONArray());
//                List<Bean> listC = bean.get("cityInfoList", new ArrayList<>());
//                for(Bean b : listC){
                  for(int i = 0; i < jsonArray.size(); i++){
                      Map map = jsonArray.getObject(i, Map.class);
                      Bean b = new Bean(map);
                      Dept d = new Dept()
                              .setID("D" + b.get("id", ""))
                              .setP_ID(id)
                              .setNAME(b.get("name", ""))
                            ;
                      deptsChild.add(d);
                }
            }

            log.info("save depts " + depts.size() + " deptsChild " + deptsChild.size());
            if(depts.size() > 0){
                Page page = new Page().setShownum(Config.getDbsize()).setNowpage(1).setNum(depts.size());
                while(page.getNowpage() <= page.getPagenum()) {
                    deptService.saveAll(depts.subList(page.start(), Math.min(page.stop(), depts.size())));
                    page.setNowpage(page.getNowpage() + 1);
                }
            }
            if(deptsChild.size() > 0){
                Page page = new Page().setShownum(Config.getDbsize()).setNowpage(1).setNum(deptsChild.size());
                while(page.getNowpage() <= page.getPagenum()) {
                    deptService.saveAll(deptsChild.subList(page.start(), Math.min(page.stop(), deptsChild.size())));
                    page.setNowpage(page.getNowpage() + 1);
                }
            }
            log.info("update area ok");

        } catch (Exception e) {
            log.error("update area error ????????????", e);
            log.error(depts.toString());
            log.error(deptsChild.toString());

        }


    }

    /**
     * 国家统计局 地区分级 遍历抓取数据
     */
    public void updateArea(){
        log.info("sync area begin thread");
//        Area root = getCityRoot();
        //此处按照一级省 分别递归获取树
        Area root = getCityRootChina();
        getCity(root, false, null);
        areaService.saveAll(Arrays.asList(root));

        for(int i = 0; i < root.getChilds().size() && i < 10; i++){
            Area item = root.getChilds().get(i);

            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    Watch watch = new Watch("area.sync." + item.getNAME());
                    getCity(item, true, null);  //html获取构建省 树
                    watch.cost("http");

                    List<Area> list = tree2list(item, root.getID(), root.getPATH(), root.getPATH_NAME());   //递归构建树
                    watch.put(list.size());
                    watch.cost("tree");


                    for(int i = 0; i < list.size(); i+= Config.getDbsize()){
                        List<Area> ll = list.subList(i, Math.min(i + Config.getDbsize(), list.size()));
                        Tools.formatOut(ll);

                        areaService.saveAll(ll);
                    }
                    watch.put("batch", list.size() / Config.getDbsize());
                    watch.cost("db");
                    log.info(watch.toPrettyString());
                }
            });
        }
        log.info("sync area end");

    }


    /**
     * 递归树 构建每个父子关系并 便利出list方便存储
     * @param area
     * @param pid
     * @param idPath
     * @param namePath
     * @return
     */
    public List<Area> tree2list(Area area, String pid, String idPath, String namePath){
        List<Area> res = new ArrayList<>();
        idPath = idPath + "/" + area.getID();
        namePath = namePath + "/" + area.getNAME();

        area.setP_ID(pid);
        area.setPATH(idPath);
        area.setPATH_NAME(namePath);
        area.setS_MTIME(TimeUtil.getTimeYmdHmss());
        area.setS_FLAG(Config.TRUE);
        res.add(area);

        for(Area ci : area.getChilds()){
            res.addAll(tree2list(ci, area.getID(), idPath, namePath));
        }

        return res;
    }




    /**
     * 获取china root
     * @return
     */
    public Area getCityRootChina(){
        Area root = new Area().setUrl("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/index.html");
        root.setID("0").setCODE("").setNAME("China").setLEVEL(""+0).setPATH("/" + root.getID()).setPATH_NAME("/" + root.getNAME());
        return root;
    }

    /**
     *  遍历节点 字节点 生成树 或 获取list
     *
     * @param parent    root节点
     * @param ifChild 是否递归子节点
     * @param list  若不为null 则把节点都添加进去   大量数据不应当使用
     */
    public void getCity(Area parent, boolean ifChild, List<Area> list){
        try {
//            http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/index.html
//            http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/51.html
            String html1 = new HttpBuilder(parent.getUrl() + "?_t=" + System.currentTimeMillis(), HttpBuilder.Type.GET)
                    .setConnectTimeout(3000).setRequestTimeout(5000).setSocketTimeout(5000)
                    .setEncode("utf-8").setDecode("gbk").buildString();
/*
<tr class="citytr">
    <td><a href="51/5101.html">510100000000</a></td><td><a href="51/5101.html">成都市</a></td>
</tr>
*/
//            Tools.out(html1);
            Document doc = Jsoup.parse(html1);
            Elements elements = null;
            String[] cs = { "provincetr", "citytr", "countytr", "towntr", "villagetr"};
            String type = "";
            for(int i = 0;  i < cs.length; i++) {
                type = cs[i];
                elements = doc.select("tr[class=" + type + "]");//.select("a");
                if(elements != null && elements.size() > 0) break;
            }
            int newLevel = parent.getLEVEL() + 1;
            String urlBase = FileUtil.getFilePath(parent.getUrl());

            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                String url = "", code = "", code1 = "", name = "";

                if (type.equalsIgnoreCase(cs[0]) ) {  //省 特殊
                    Elements elementCols = element.select("td").select("a");
                    for (int j = 0; j < elementCols.size(); j++) {
                        Element elementCol = elementCols.get(j);
                        url = elementCol.attr("href");
                        if (url != null && url.length() > 0) {
                            url = urlBase + "/" + url;
                        }
                        code = FileUtil.getFileNameOnly(url);
                        name = elementCol.text();

                        Area area = new Area().setUrl(url);
                        area.setID(code).setNAME(name).setCODE(code1).setLEVEL("" + newLevel);
                        Tools.out(newLevel, url, name, code, code1);

                        //                深度构建树  或者 广度构建    由上而下  由下而上 ？
                        parent.addChilds(Arrays.asList(area));
                        if (list != null) {
                            list.add(area);
                        }
                        if (url != null && url.length() > 0 && ifChild) {
                            getCity(area, ifChild, list);
                        }
                    }
                }else {
                    if (type.equalsIgnoreCase(cs[4])) { //镇 特殊
                        Elements elementLineV = element.select("td");
                        if (elementLineV.size() >= 3) {
                            code = elementLineV.get(0).text();
                            code1 = elementLineV.get(1).text();
                            name = elementLineV.get(2).text();
                        }
                    } else {
                        Elements elementLine = element.select("td").select("a");
                        if (elementLine.size() > 0) {
                            url = elementLine.get(0).attr("href");
                        }
                        if (url != null && url.length() > 0) {
                            url = urlBase + "/" + url;
                        }
                        code = elementLine.get(0).text();
                        name = elementLine.get(1).text();
                    }
                    Area area = new Area().setUrl(url);
                    area.setID(code).setNAME(name).setCODE(code1).setLEVEL("" + newLevel);
                    Tools.out(newLevel, url, name, code, code1);

                    //                深度构建树  或者 广度构建    由上而下  由下而上 ？
                    parent.addChilds(Arrays.asList(area));
                    if (list != null) {
                        list.add(area);
                    }
                    if (url != null && url.length() > 0 && ifChild) {
                        getCity(area, ifChild, list);
                    }
                }
            }
        }catch (Exception e){
        }

    }



}

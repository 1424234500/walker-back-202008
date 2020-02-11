package com.walker.service.impl;


import com.walker.common.util.*;
import com.walker.service.Config;
import com.walker.mode.Area;
import com.walker.service.AreaService;
import com.walker.service.SyncAreaService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 初始化数据服务
 */
@Service("syncAreaService")
public class SyncAreaServiceImpl implements SyncAreaService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    AreaService areaService;

    /**
     * 国家统计局 地区分级 遍历抓取数据
     */
    public void syncArea(){
        log.info("sync area begin thread");
//        Area root = getCityRoot();
        //此处按照一级省 分别递归获取树
        Area root = getCityRootChina();
        getCity(root, false, null);
        areaService.saveAll(Arrays.asList(root));

        for(int i = 0; i < root.getChilds().size() && i < 998; i++){
            Area item = root.getChilds().get(i);

//            //单省作为线程
//            ThreadUtil.execute(new Runnable() {
//                @Override
//                public void run() {
                    Watch watch = new Watch("area.sync." + item.getNAME());
                    getCity(item, true, null);  //html获取构建省 树
                    watch.cost("http");

                    List<Area> list = tree2list(item, root.getID(), root.getPATH(), root.getPATH_NAME());   //递归构建树
                    watch.put(list.size());
                    watch.cost("tree");


                    for(int ii = 0; ii < list.size(); ii+= Config.getDbsize()){
                        List<Area> ll = list.subList(ii, Math.min(ii + Config.getDbsize(), list.size()));
//                        Tools.formatOut(ll);
                        log.info("batch save " + ii + " of " + list.size() );
                        areaService.saveAll(ll);
                    }
                    watch.put("batch", list.size() / Config.getDbsize());
                    watch.cost("db");
                    log.info(watch.toPrettyString());
                }
//            });
//        }
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
            Thread.sleep(50);   //休眠间隔避免 高频率导致封ip？
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
                        url = "";
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
//<tr class="countytr"><td><a href="01/130105.html">130105000000</a></td><td><a href="01/130105.html">新华区</a></td></tr>
                        Elements elementLine = element.select("td").select("a");
                        url = "";
                        if (elementLine.size() > 0) {
                            url = elementLine.get(0).attr("href");
                        }else{
                            elementLine = element.select("td");
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

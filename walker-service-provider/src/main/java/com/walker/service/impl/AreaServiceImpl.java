package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.common.util.TimeUtil;
import com.walker.config.Config;
import com.walker.dao.AreaRepository;
import com.walker.dao.RoleUserRepository;
import com.walker.mode.Area;
import com.walker.service.AreaService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

@Service("areaService")
public class AreaServiceImpl implements AreaService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AreaRepository areaRepository;


    /**
     * 构建机构树
     * @param objs
     * @return
     */
    @Override
    public List<Area> saveAll(List<Area> objs) {
        Set<String> pids = new LinkedHashSet<>();
        for(Area obj : objs){
            if(obj.getP_ID() != null)   //可能没有上级
                pids.add(obj.getP_ID());
        }
        List<Area> pobjs = pids.size() > 0 ? areaRepository.findAllByID(pids) : new ArrayList<>();    //查找出所有上级
        Map<String, Area> index = new HashMap<>();
        for(Area obj : pobjs){
            index.put(obj.getID(), obj);
        }

        List<Area> oks = new ArrayList<>();
        for(Area obj : objs){
            if(obj.getP_ID() != null && obj.getP_ID().length() > 0){
                Area pobj = index.get(obj.getP_ID());
                if(pobj == null){//表中 上级不存在
                    log.error("try save area not exists pid " + obj);
                }else{//上级存在 复用机构树
                    obj.setPATH(pobj.getPATH() + "," + obj.getID());
                    obj.setPATH_NAME(pobj.getPATH_NAME() + "/" + obj.getNAME());
                    oks.add(obj);
                }
            }else{//无上级 root
                obj.setPATH(obj.getID());
                obj.setPATH_NAME(obj.getNAME());
                oks.add(obj);
            }
        }
        for(Area obj : oks){
            if(StringUtils.isEmpty(obj.getS_MTIME())){
                obj.setS_MTIME(TimeUtil.getTimeYmdHms());
            }
        }

        return areaRepository.saveAll(oks);
    }

    @Override
    public Integer delete(Area obj) {
        deleteAll(Arrays.asList(obj.getID()));
        return 1;
    }

    @Override
    public Area get(Area obj) {
        Optional<Area> result = areaRepository.findById(obj.getID());
        return result.isPresent()?result.get():null;
    }

    @Override
    public List<Area> finds(Area obj, Page page) {
        Pageable pageable = Config.turnTo(page);
        org.springframework.data.domain.Page<Area> res = areaRepository.findAll(this.getSpecification(obj), pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public Integer count(Area obj) {
        long res = areaRepository.count(this.getSpecification(obj));
        return new Long(res).intValue();
    }

    @Override
    public List<Area> findsRoot(Page page) {
        Pageable pageable = Config.turnTo(page);
        org.springframework.data.domain.Page<Area> res = areaRepository.findsRoot(pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();    }

    private Specification getSpecification(Area obj){
        return new Specification<Area>(){
            @Override
            public Predicate toPredicate(Root<Area> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotEmpty(obj.getID())) {
                    list.add(criteriaBuilder.like(root.get("ID"), "%" + obj.getID() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getS_MTIME())) {
                    list.add(criteriaBuilder.greaterThan(root.get("S_MTIME"), obj.getS_MTIME()));
                }
                if (StringUtils.isNotEmpty(obj.getS_FLAG())) {
                    list.add(criteriaBuilder.equal(root.get("S_FLAG"), obj.getS_FLAG()));
                }

                if (StringUtils.isNotEmpty(""+obj.getLEVEL())) {
                    list.add(criteriaBuilder.equal(root.get("LEVEL"), obj.getLEVEL()));
                }

                if (StringUtils.isNotEmpty(obj.getNAME())) {
                    list.add(criteriaBuilder.like(root.get("NAME"), "%" + obj.getNAME() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getP_ID())) {
                    list.add(criteriaBuilder.like(root.get("P_ID"), "%" + obj.getP_ID() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getPATH())) {
                    list.add(criteriaBuilder.like(root.get("PATH"), "%" + obj.getPATH() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getPATH_NAME())) {
                    list.add(criteriaBuilder.like(root.get("PATH_NAME"), "%" + obj.getPATH_NAME() + "%"));
                }
                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };
    }

    @Override
    public Integer[] deleteAll(List<String> ids) {
        Integer[] res = new Integer[ids.size()];
        int i = 0;
        for(String areaId : ids) {
            List<Area> areas = areaRepository.findAllByPATH(areaId);
            Set<String> areaIds = new LinkedHashSet<>();
            areaIds.add(areaId);
            for(Area obj : areas){
                areaIds.add(obj.getID());
            }
            int cc = 0;
            if(areaIds.size() > 0) {
                cc = areaRepository.selfDeleteAll(areaIds);
            }
            res[i++] = cc;

        }
        return res;
    }
}

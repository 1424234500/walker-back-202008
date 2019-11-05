package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.config.Config;
import com.walker.dao.DeptRepository;
import com.walker.dao.RoleUserRepository;
import com.walker.mode.Dept;
import com.walker.service.DeptService;
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

@Service("deptService")
public class DeptServiceImpl implements DeptService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DeptRepository deptRepository;
    @Autowired
    private RoleUserRepository roleUserRepository;


    /**
     * 构建机构树
     * @param objs
     * @return
     */
    @Override
    public List<Dept> saveAll(List<Dept> objs) {
        List<String> pids = new ArrayList<>();
        for(Dept obj : objs){
            pids.add(obj.getP_ID());
        }
        List<Dept> pobjs = deptRepository.findAllByID(pids);
        Map<String, Dept> index = new HashMap<>();
        for(Dept obj : pobjs){
            index.put(obj.getID(), obj);
        }

        List<Dept> oks = new ArrayList<>();
        for(Dept obj : objs){
            if(obj.getP_ID() != null && obj.getP_ID().length() > 0){
                Dept pobj = index.get(obj.getP_ID());
                if(pobj == null){//上级不存在
                    log.warn("try save dept not exists pid " + obj);
                }else{//上级存在 复用机构树
                    obj.setPATH(pobj.getPATH() + "," + obj.getID());
                    oks.add(obj);
                }
            }else{//无上级 root
                obj.setPATH(obj.getID());
                oks.add(obj);
            }
        }
        return deptRepository.saveAll(oks);
    }

    @Override
    public Integer delete(Dept obj) {
        deleteAll(Arrays.asList(obj.getID()));
        return 1;
    }

    @Override
    public Dept get(Dept obj) {
        Optional<Dept> result = deptRepository.findById(obj.getID());
        return result.isPresent()?result.get():null;
    }

    @Override
    public List<Dept> finds(Dept obj, Page page) {
        Pageable pageable = Config.turnTo(page);
        org.springframework.data.domain.Page<Dept> res = deptRepository.findAll(this.getSpecification(obj), pageable);
        page.setNum(res.getTotalElements());
        return res.getContent();
    }

    @Override
    public Integer count(Dept obj) {
        long res = deptRepository.count(this.getSpecification(obj));
        return new Long(res).intValue();
    }
    private Specification getSpecification(Dept obj){
        return new Specification<Dept>(){
            @Override
            public Predicate toPredicate(Root<Dept> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> list = new ArrayList<>();
                if (StringUtils.isNotEmpty(obj.getID())) {
                    list.add(criteriaBuilder.like(root.get("ID"), "%" + obj.getID() + "%"));
                }
                if (StringUtils.isNotEmpty(obj.getS_MTIME())) {
                    list.add(criteriaBuilder.greaterThan(root.get("S_MTIME"), obj.getS_MTIME()));
                }
                if (StringUtils.isNotEmpty(obj.getS_ATIME())) {
                    list.add(criteriaBuilder.greaterThan(root.get("S_ATIME"), obj.getS_ATIME()));
                }
                if (StringUtils.isNotEmpty(obj.getS_FLAG())) {
                    list.add(criteriaBuilder.equal(root.get("S_FLAG"), obj.getS_FLAG()));
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

                return criteriaBuilder.and(list.toArray(new Predicate[0]));
            }
        };
    }

    @Override
    public Integer[] deleteAll(List<String> ids) {
        Integer[] res = new Integer[ids.size()];
        int i = 0;
        for(String deptId : ids) {
            List<Dept> depts = deptRepository.findAllByPATH(deptId);
            List<String> deptIds = new ArrayList<>();
            deptIds.add(deptId);
            for(Dept obj : depts){
                deptIds.add(obj.getID());
            }
            int cc = 0;
            if(deptIds.size() > 0) {
                cc = deptRepository.selfDeleteAll(deptIds);
                roleUserRepository.deleteAllByUserId(deptIds);
            }
            res[i++] = cc;

        }
        return res;
    }
}

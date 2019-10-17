package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.config.Config;
import com.walker.dao.FileIndexRepository;
import com.walker.dao.FileIndexRepository;
import com.walker.mode.FileIndex;
import com.walker.service.FileIndexService;
import com.walker.service.FileIndexService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service("fileIndexrService")
public class FileIndexServiceImpl implements FileIndexService {

	@Autowired
	private FileIndexRepository fileIndexrRepository;


	@Override
	public List<FileIndex> saveAll(List<FileIndex> objs) {
		return fileIndexrRepository.saveAll(objs);
	}

	@Override
	public Integer delete(FileIndex obj) {
		fileIndexrRepository.deleteById(obj.getID());
		return 1;
	}

	@Override
	public FileIndex get(FileIndex obj) {
		Optional<FileIndex> result = fileIndexrRepository.findById(obj.getID());
		return result.isPresent()?result.get():null;
	}

	@Override
	public List<FileIndex> finds(FileIndex obj, Page page) {
		Pageable pageable = Config.turnTo(page);
		org.springframework.data.domain.Page<FileIndex> res = fileIndexrRepository.findAll(this.getSpecification(obj), pageable);
		page.setNum(res.getTotalElements());
		return res.getContent();
	}

	@Override
	public Integer count(FileIndex obj) {
		long res = fileIndexrRepository.count(this.getSpecification(obj));
		return new Long(res).intValue();
	}
	private Specification getSpecification(FileIndex obj){
		return new Specification<FileIndex>(){
			@Override
			public Predicate toPredicate(Root<FileIndex> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

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
				if (StringUtils.isNotEmpty(obj.getEXT())) {
					list.add(criteriaBuilder.like(root.get("EXT"), "%" + obj.getEXT() + "%"));
				}
				if (StringUtils.isNotEmpty(obj.getNAME())) {
					list.add(criteriaBuilder.like(root.get("NAME"), "%" + obj.getNAME() + "%"));
				}
				if (StringUtils.isNotEmpty(obj.getLEVEL())) {
					list.add(criteriaBuilder.greaterThan(root.get("LEVEL"), obj.getLEVEL()));
				}
				if (StringUtils.isNotEmpty(obj.getLENGTH())) {
					list.add(criteriaBuilder.greaterThan(root.get("LENGTH"), obj.getLENGTH()));
				}
				if (StringUtils.isNotEmpty(obj.getOWNER())) {
					list.add(criteriaBuilder.like(root.get("OWNER"), "%" + obj.getNAME() + "%"));
				}
				if (StringUtils.isNotEmpty(obj.getPATH())) {
					list.add(criteriaBuilder.like(root.get("PATH"), "%" + obj.getPATH() + "%"));
				}
				if (StringUtils.isNotEmpty(obj.getINFO())) {
					list.add(criteriaBuilder.like(root.get("INFO"), "%" + obj.getINFO() + "%"));
				}
				return criteriaBuilder.and(list.toArray(new Predicate[0]));
			}
		};
	}

	@Override
	public Integer[] deleteAll(List<String> ids) {
		int res = fileIndexrRepository.selfDeleteAll(ids);
		return new Integer[]{res};
	}
}

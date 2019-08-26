package com.walker.service.impl;

import com.walker.common.util.*;
import com.walker.core.database.SqlUtil;
import com.walker.service.StudentService;
import com.walker.web.dao.mybatis.BaseMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Transactional
@Service("studentServiceMybatis")
public class StudentServiceImplMybatis implements StudentService, Serializable {

	private static final long serialVersionUID = 8304941820771045214L;
 
	/**
	 * mybatis入口
	 */
	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Autowired
	protected BaseMapper baseMapper;

	@Override
	public List<Map<String, Object>> finds(String id, String name, String sFlag, String timefrom, String timeto, Page page) {
		if(!Tools.notNull(name)) name = "";
		else name = SqlUtil.like(name);
		if(!Tools.notNull(id)) id = "";
		else  id = SqlUtil.like(id);
		
		Map map = MapListUtil.getMap()
				.put("NAME", name)
				.put("ID", id)
				.put("S_FLAG", sFlag)
				.put("TIMEFROM", timefrom)
				.put("TIMETO", timeto)
				.put("PAGESTART", page.start())
				.put("PAGESTOP", page.stop())
				.build();  
		page.setNum(baseMapper.count(map));
		return baseMapper.find(map);
	}

	@Override
	public int update(String id, String name, String time) {
		Map map = MapListUtil.getMap()
				.put("ID", id)
				.put("NAME", name)
				.put("S_MTIME", time)
				.build(); 
		return baseMapper.update(map);
	}

	@Override
	public int delete(String id) {
//		Map map = MapListUtil.getMap() 
//				.put("id", id)
//				.build();
//		return baseMapper.delete(map);
		
		Map map = MapListUtil.getMap()
				.put("ID", id)
				.put("S_FLAG", Context.NO)
				.put("S_MTIME", TimeUtil.getTimeYmdHmss())
				.build(); 
		return baseMapper.update(map);
	}

	@Override
	public Map<String, Object> find(String id) {
		Map<String, Object> map = MapListUtil.getMap() 
				.put("ID", id)
				.build();
		List<Map<String, Object>> list = baseMapper.find(map);
		Map<String, Object> res = null;
		if(list != null && list.size() > 0){
			res = list.get(0);
		}else{
			res = new HashMap();
		}
		return res;
	}

	@Override
	public int add(String name, String time) {
		Map map = MapListUtil.getMap() 
				.put("ID", LangUtil.getGenerateId())
				.put("NAME", name)
				.put("S_MTIME", TimeUtil.getTimeYmdHmss())
				.put("S_FLAG", Context.YES)
				.build();
		return baseMapper.add(map);
	}

}
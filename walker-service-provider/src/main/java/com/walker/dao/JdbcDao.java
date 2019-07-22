package com.walker.dao;

import com.walker.core.database.BaseDao;
import com.walker.core.database.BaseDaoAdapter;
import com.walker.core.database.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository("jdbcDao")
public class JdbcDao extends BaseDaoAdapter {

    @Autowired
    JdbcTemplate jdbcTemplate;

    String ds = "mysql";
    @Override
    public void setDs(String ds) {
        this.ds = ds;
    }

    @Override
    public String getDs() {
        return ds;
    }

    @Override
    public List<Map<String, Object>> find(String sql, Object... params) {
        return jdbcTemplate.queryForList(sql, params);
    }

    @Override
    public Integer executeSql(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }

    @Override
    public Integer[] executeSql(String sql, List<List<Object>> objs) {
        List<Object[]> args = new ArrayList<>();
        for(List<Object> arg : objs){
            args.add(arg.toArray());
        }
        int resint[] = jdbcTemplate.batchUpdate(sql, args);
        Integer[] res = new Integer[resint.length];
        for(int i = 0; i< resint.length; i++){
            res[i] = resint[i];
        }
        return res;
    }

    @Override
    public Integer executeProc(String proc, Object... objects) {
        return 0;
    }
}

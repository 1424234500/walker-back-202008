package com.walker.core.database;

import java.util.List;
import java.util.Map;

public interface BaseJdbc {

    /**
     * 数据源 不同数据源 不同实现方式 分页 ddl dml
     *
     * @param ds
     */
    public void setDs(String ds);
    public String getDs() ;




/////////////////////////////////必须实现 其他依赖以下接口
    /**
     * 获得结果集
     *
     * @param sql    SQL语句
     * @param params 参数
     * @return 结果集
     */
    public List<Map<String, Object>> find(String sql, Object... params);

    /**
     * 执行SQL语句
     *
     * @return 响应行数
     */
    public Integer executeSql(String sql, Object... params);

    /**
     * 批量执行sql
     *
     * @return
     */
    public Integer[] executeSql(String sql, List<List<Object>> objs);

    /**
     * 执行存储过程 最后一个占位?返回值
     *
     * @param proc    "{call countBySal(?,?)}"
     * @param objects
     * @return
     */
    public Integer executeProc(String proc, Object... objects);
}

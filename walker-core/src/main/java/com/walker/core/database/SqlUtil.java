package com.walker.core.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.walker.common.util.FileUtil;
import com.walker.common.util.Tools;
import com.walker.core.aop.Fun;
import com.walker.core.exception.ErrorException;

/**
 * 数据库sql语句帮助
 * @author Walker
 * 2017年9月18日17:33:25
 */
public class SqlUtil{ 
	 
	
	/**
	 * 字符串模糊查询 添加符号  "%" + value + "%" 
	 */
	public static  String like(String value){
		return "%" + value + "%";
	}
	/**
	 * 字符串 单引号包围"'" + value + "'" 
	 */
	public static  String include(String value){
		return "'" + value + "'";
	}
	/**
	 * 年月日 时分秒 格式'yyyy-mm-dd hh24:mi:ss'
	 */
	public static String getTimeFormatL(){
		return "'yyyy-mm-dd hh24:mi:ss'";
	}
	/**
	 * 年月日 格式'yyyy-mm-dd'
	 */
	public static String getTimeFormatS(){
		return "'yyyy-mm-dd'";
	}
	/**
	 * 转换 to_date 年月日 时分秒 to_date('time','yyyy-mm-dd hh24:mi:ss')
	 */
	public static  String to_dateL(String time){
		return " to_date(" + include(time) + ", " + getTimeFormatL() + ") ";
	}
	/**
	 * 转换 to_date 年月日 时分秒 to_date(?,'yyyy-mm-dd hh24:mi:ss')
	 */
	public static  String to_dateL(){
		return " to_date(?, " + getTimeFormatL() + ") ";
	}
	/**
	 * 转换 to_date 年月日   to_date('time','yyyy-mm-dd')
	 */
	public static  String to_dateS(String time){
		return " to_date(" + include(time) + ", " + getTimeFormatS() + ") ";
	}
	/**
	 * 转换 to_date 年月日  to_date(?,'yyyy-mm-dd')
	 */
	public static  String to_dateS( ){
		return " to_date(?, " + getTimeFormatS() + ") ";
	}
	/**
	 * 转换 to_char 年月日 时分秒
	 */
	public  static String to_charL(String time){
		return " to_char(" + include(time) + ", " + getTimeFormatL() + ") ";
	}	
	/**
	 * 转换 to_char 年月日 时分秒
	 */
	public static  String to_charL( ){
		return " to_char(?, " + getTimeFormatL() + ") ";
	}
	/**
	 * 转换 to_char 年月日 
	 */
	public static  String to_charS(String time){
		return " to_char(" + include(time) + ", " + getTimeFormatS() + ") ";
	}
	/**
	 * 转换 to_char 年月日 
	 */
	public static  String to_charS( ){
		return " to_char(?, " + getTimeFormatS() + ") ";
	}
	/**
	 * 位数补齐 用c补齐到tolen位
	 */
	public static  String file(String value, int tolen, char c){
		return " lpad(" + value + ", " + tolen + ", '" + c + "') ";
	}
	
	
	/**
	 * ?, ?, ?
	 */
	public static String makeMapPosis(Map<?, ?> map){
		return makePosition("?", map.size());
	}
	/**
	 * id, value, name
	 */
	public static String makeMapKeys(Map<?, ?> map){
		String res = "  ";
		for(Object key : map.keySet()){
			res = res + key + ", ";
		}
		res = res.substring(0, res.length() - 2);
		return res;
	}
	/**
	 * id=?, value=?, name=?
	 */
	public static String makeMapKeyPosis(Map<?, ?> map){
		String res = "";
		for(Object key : map.keySet()){
			res = res + key + "=?, ";
		}
		res = res.substring(0, res.length() - 2);
		return res;
	}
	/**
	 * id1, value2, name3
	 */
	public static String makeMapValues(Map<?, ?> map){
		String res = "  ";
		for(Object key : map.keySet()){
			res = res + (map.get(key)).toString() + ", ";
		}
		res = res.substring(0, res.length() - 2);
		return res;
	}
	public static int getMapSize(Map<?, ?> map){
		return map.size();
	}
	/**
	 * 设置n个问号 ？ 占位符
	 */
	public static String makePosition(String posi, int num){
		String res = "";
		for(int i = 0; i < num - 1; i++){
			res = res + posi + ", ";
		}
		res = res + posi;
		return res;
	}
	/**
	 * sql占位符替换拼接参数 生成sql  仅作参考sql语句使用 易被sql注入
	 */
	public static String makeSql(String sql, Object...objects){
		if(sql.length() <= 0)return sql;
		if(sql.charAt(sql.length() - 1) != ' '){	//最后面加空格 因为split最后的符号不分
			sql = sql + " ";
		}
		
		int needLen = sql.split("\\?").length - 1; 
		int realLen = 0;
		if (objects != null &&  objects.length > 0) {
			realLen = objects.length;
		} 
		if(needLen != realLen){
			sql = "Error! "
					+ "\nsql: " + sql  
					+ "\nobjects: " + Tools.objects2string(objects) 
					+ "\n占位符个数[" + needLen + "]与实际参数个数[" + realLen + "]不同";
		}else if(needLen > 0){//sf s where id=? and name=? order by ? 。
			int t = -1;
			for (int i = 0; i < objects.length; i++) {
				t = sql.indexOf("?"); 
				sql = sql.substring(0, t) + (objects[i]==null?"null":"'" + objects[i] + "'") + sql.substring(t+1);
			}
		}
		return sql;
	}
	
	/**
	 * sql特殊字符过滤  字符兼容
	 * @param sql
	 * @return
	 */
	public static String filter(String sql) {
		if(sql == null || sql.length() == 0) {
			throw new ErrorException("sql is null ?");
		}
		sql = StringUtils.strip(sql);
		if(sql.endsWith(";")) {
			sql = sql.substring(0, sql.length() - 1);
		}

		return sql;
	}
	/**
	 * 根据不同数据源构造不同分页sql
	 * @param sql
	 * @param page
	 * @param rows
	 * @return
	 */
	public static String makeSqlPage(String dsName, String sql, int page, int rows) {
		dsName = String.valueOf(dsName);
		int start = ((page - 1) * rows);
		if(dsName.equals("mysql") || dsName.equals("sqlite")) {
			sql = "select * from ( " + sql + " ) t limit " + start + "," + rows;	//2,5 -> 56789 -> 5,5
		}else if(dsName.equals("oracle")) {										//2,5 -> 67890 -> 5,10
			int stop = page * rows + 1;
			sql = " select * from ( select t.*,rownum rowno from ( "
			        + sql + 
			 " ) t where rownum <=  " + stop + " ) where rowno > " + start;
		}else{
			throw new ErrorException("no implements  " + dsName);
		}		
		return sql;
	}
	/**
	 * 表列查询 sql
	 * @param dsName
	 * @param tableName
	 * @return
	 */
	public static String makeSqlColumn(String dsName, String tableName) {
		dsName = String.valueOf(dsName);
//oracle
//SELECT COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE TABLE_NAME = upper('" + tableName + "') ORDER BY COLUMN_ID";
//sqlserver
//select name COLUMN_NAME from syscolumns where id=object_id('表名');
//mysql
//select COLUMN_NAME from information_schema.columns where table_name='tablename'
	
		
		String sql = "";
		if(dsName.equals("mysql")) {
			sql = "select COLUMN_NAME from information_schema.COLUMNS where table_name = '" + tableName + "' ORDER BY COLUMN_NAME";
		}else if(dsName.equals("oracle")) {
			sql = "SELECT COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE TABLE_NAME = upper('" + tableName + "') ORDER BY COLUMN_ID";
		}else{
			throw new ErrorException("no implements  " + dsName);
		}
		return sql;
	}
	/**
	 * 表列查询 sql
	 * @param dsName
	 * @param tableName
	 * @return
	 */
	public static String makeSqlColumnNickname(String dsName, String tableName) {
		dsName = String.valueOf(dsName);
/*
oracle
SELECT COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE TABLE_NAME = upper('" + tableName + "') ORDER BY COLUMN_ID";
sqlserver
SELECT A.name AS table_name, B.name AS name, C.value AS nickname FROM sys.tables A INNER JOIN sys.columns B ON B.object_id = A.object_id LEFT JOIN sys.extended_properties C ON C.major_id = B.object_id AND C.minor_id = B.column_id WHERE A.name = 'TEACHER'
mysql
select COLUMN_NAME,COLUMN_COMMENT from INFORMATION_SCHEMA.COLUMNS where table_name = 'TEACHER'
;
*/

		String sql = "";
		if(dsName.equals("mysql")) {
			sql = "select COLUMN_NAME,COLUMN_COMMENT from INFORMATION_SCHEMA.COLUMNS where table_name = '" + tableName + "' ";
		}else if(dsName.equals("oracle")) {
			sql = "select COLUMN_NAME,COLUMN_COMMENT from INFORMATION_SCHEMA.COLUMNS where table_name = '" + tableName + "' ";
		}else{
			throw new ErrorException("no implements  " + dsName);
		}
		return sql;
	}
	/**
	 * 结果集转换 列 值
	 * @param rs
	 * @return [ { id:1,name:n1}, {id:2,name:n2}  ]
	 * @throws SQLException 
	 */
	public static List<Map<String, Object>> toListMap(ResultSet rs) throws SQLException {
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		ResultSetMetaData md = rs.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
		int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
		while (rs.next()) {
			Map<String, Object> map = new HashMap<>(columnCount);
			for (int i = 1; i <= columnCount; i++) {
				map.put(md.getColumnName(i), rs.getObject(i));
			}
			res.add(map);
		}
		return res;
	}
	/**
	 * 结果集转换 值
	 * @param rs
	 * @return	[ [1, n1], [2,n2] ]
	 * @throws SQLException
	 */
	public static List<List<String>> toValues(ResultSet rs) throws SQLException {
		List<List<String>> res = new ArrayList<List<String>>();
		ResultSetMetaData md = rs.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
		int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
		while (rs.next()) {
			ArrayList<String> list = new ArrayList<String>(columnCount);
			for (int i = 1; i <= columnCount; i++) {
					list.add(String.valueOf(rs.getObject(i)));
			}
			res.add(list);
		}
		return res;
	}
	/**
	 * 结果集转换 值
	 * @param rs
	 * @return	[ [1, n1], [2,n2] ]
	 * @throws SQLException
	 */
	public static List<List<String>> toValues(List<Map<String,Object>> rs)   {
		List<List<String>> res = new ArrayList<List<String>>();
		for(Map<String,Object> map : rs){
			ArrayList<String> list = new ArrayList<String>();
			for (String key : map.keySet()) {
				list.add(String.valueOf(map.get(key)));
			}
			res.add(list);
		}
		return res;
	}
	/**
	 * 结果集转换 列
	 * @param rs
	 * @return	[ id, name ]
	 * @throws SQLException
	 */
	public static List<String> toKeys(ResultSet rs) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount();
		List<String> res = new ArrayList<>(columnCount);
		for(int i=0; i < columnCount; i++){
			res.add(md.getColumnName(i+1));
		}
		return res;
	}


	/**
	 * 结果集转换 列
	 * @param rs
	 * @return	[ id, name ]
	 * @throws SQLException
	 */
	public static List<String> toKeys(List<Map<String,Object>> rs)  {
		List<String> res = new ArrayList<>();

		if(rs.size() > 0){
			Map<String, Object> map = rs.get(0);
			res.addAll(map.keySet());
		}
		return res;
	}

	private static Logger log = Logger.getLogger(SqlUtil.class);
	/**
	 * 执行sql文件
	 * @param baseDao
	 * @param path
	 */
	public static void executeSqlFile(BaseDao baseDao, String path) {
		FileUtil.readByLines(path, new Fun<String>() {
			@Override
			public <T> T make(String obj) {
				try {
					obj = StringUtils.strip(obj);
					if(!obj.startsWith("--") && obj.length() > 0) {
						log.info(obj);
						baseDao.executeSql(obj);
					}
				}catch(Exception e) {
					log.error(e);
				}
				return null;
			}
		}, null);		
	}

	/**
	 * 计数sql
	 * @param sql
	 * @return
	 */
	public static String makeSqlCount(String sql) {
		return "select count(*) from ("+sql+") t ";
	}
	 
	
	/**
	 * 排序sql
	 * @param sql
	 * @param order
	 * @return
	 */
	public static String makeSqlOrder(String sql, String order) {
		if(order != null && order.length() > 0) {
			return "select * from ("+sql+") t order by " + order;
		}
		return sql;
	}
	
	/**
	 * 分表 表名生成 
	 * 分表依赖键值 至少一个
	 * 分表数量
	 */
	public static int makeTableCount(int count, String...keys) {
		String key = makeTableKey(keys);
		return Math.abs(key.hashCode()) % count;
	}
	/**
	 * 生成分表键 无序
	 */
	public static String makeTableKey(String...keys) {
		Arrays.sort(keys);
		StringBuilder key = new StringBuilder();
		for(String str : keys) {
			key.append(str).append(":");
		}
		return key.toString();
	}
	
}
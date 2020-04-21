package com.walker.core.annotation;

import com.walker.common.util.Tools;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 注解使用样例
 *
 * */
@DBTable(name = "EG_DB_TABLE")
public class EgDBTable {

	@DBSQLString(value = 256, name = "ID", DBConstraints = @DBConstraints(primaryKey = true))
	String id;


	@DBSQLString(name = "NAME", DBConstraints = @DBConstraints(allowNull = true, unique = true))
	String name;

	@DBSQLInteger
	String age;





}
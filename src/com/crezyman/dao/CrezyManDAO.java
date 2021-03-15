package com.crezyman.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.crezyman.annotation.NotField;
import com.crezyman.annotation.Table;
import com.crezyman.utils.Params;
import com.crezyman.utils.StringUtils;


@SuppressWarnings("finally")
public class CrezyManDAO extends AbstractBaseDaoImpl implements ModelDao {
	
	private static String PK;
	private static String UPDATE_FIELDS;
	private static String[] ORM_FIELDS;
	private static String[] SELECT_FIELDS;
	private static String SELECT_FIELDS_TOSTRING;

	public CrezyManDAO(Connection connection) throws Exception {
		super(connection);
		List<String> o = super.getTableFields();
		PK = (String) o.get(0);
		UPDATE_FIELDS = (String) o.get(3);
		ORM_FIELDS = o.get(1).split("\\, ");
		SELECT_FIELDS =   o.get(2).split("\\, ");
		SELECT_FIELDS_TOSTRING = Arrays.toString(SELECT_FIELDS).substring(1,
				Arrays.toString(SELECT_FIELDS).length() - 1);
	}




	/**
	 * 对象关系映射
	 */
	@Override
	public Object tableToClass(ResultSet rs) throws Exception {
		Object obj = super.orm(rs, ORM_FIELDS);
		return obj;
	}

	@Override
	public void update(Params params,Object entity) throws Exception {
		List<Object> paramsList = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer(
				" update `" + super.getTableName() + "` set " + UPDATE_FIELDS + " where  1 = 1 ");
		entitySetToParamsList(paramsList,entity);
		
		Map<String, Object> make = super.getParamsList(params);
		sql.append(make.get("sql"));
		Object obj = make.get("paramsList");
		if (obj instanceof ArrayList<?>) {
			for (Object o : (List<?>) obj) {
				paramsList.add(Object.class.cast(o));
			}
		}

		
		this.executeUpdate(sql.toString(), paramsList.toArray());
	}
	
	/**
	 * 更新支持对象传参
	 * @param paramsList  更新条件
	 * @param entity   更新对象
	 * @throws Exception
	 */
	private static void entitySetToParamsList(List<Object> paramsList,Object entity) throws Exception {
		Class<?> clz = entity.getClass();
		
		Field[] fieldList = clz.getDeclaredFields();
		Method method = null;
	
		LinkedHashMap<String,String> allFieldsInfo = new LinkedHashMap<>();
		for(Field s:fieldList) {
			if(!s.isAnnotationPresent(NotField.class)) {
				allFieldsInfo.put(s.getName(), s.getType().toString());
			}
		}
		
		boolean bool = false;
		int i = 0;
		for(Map.Entry<String, String> entry : allFieldsInfo.entrySet()) {
			bool = StringUtils.isAcronym(entry.getKey());
			if(!bool) {
				method = clz.getMethod("get" + StringUtils.captureName(entry.getKey()) );
			}else {
				method = clz.getMethod("get" + entry.getKey());
			}
			Object object = method.invoke(entity);
			if(i > 0) {
				paramsList.add(object);  
			}
			i++;
        }
	}

	/**
	 * 保存
	 * 
	 * @param order
	 * @throws Exception 
	 * @throws SQLException
	 */
	public int save(Object entity) throws Exception {   // 保存
		Integer id = 0;
		String sql = "insert into `" + super.getTableName() + "`(" + SELECT_FIELDS_TOSTRING + ") values("
				+ super.genarateWhy(SELECT_FIELDS.length) + ") ";

		List<Object> paramsList = new ArrayList<Object>();
		
		entitySetToParamsList(paramsList,entity);
		
		try {
			id = this.executeInsert(sql, paramsList.toArray());
			return new Integer(id).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.closeResource();
		}
		return id.intValue();
	}
	

	@Override
	public void deleteById(Integer id) {
		String sql = " delete from `" + super.getTableName() + "` where " + PK + " = ? ";
		Object params[] = new Object[] { id };
		try {
			this.executeUpdate(sql.toString(), params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.closeResource();
		}
	}

	@Override
	public Object getEntityById(Integer id) {
		String sql = " select * from `" + super.getTableName() + "` where " + PK + " = ? ";
		ResultSet resultSet = null;
		Object obj = null;
		try {
			Object params[] = new Object[] { id };
			resultSet = this.executeQuery(sql, params);
			while (resultSet.next()) {
				obj = tableToClass(resultSet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.closeResource(resultSet);
			this.closeResource();
			return obj;
		}
	}

	public List<Object> queryEntityList(Params params) {
		List<Object> paramsList = new ArrayList<Object>();
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer(
				" select " + PK + ", " + SELECT_FIELDS_TOSTRING + " from `" + super.getTableName() + "`  where 1=1 ");

		Map<String, Object> make = super.getParamsList(params);
		sql.append(make.get("sql"));

		Object obj = make.get("paramsList");
		if (obj instanceof ArrayList<?>) {
			for (Object o : (List<?>) obj) {
				paramsList.add(Object.class.cast(o));
			}
		}

		ResultSet resultSet = this.executeQuery(sql.toString(), paramsList.toArray());
		try {
			while (resultSet.next()) {
				Object rst = this.tableToClass(resultSet);
				list.add(rst);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.closeResource();
			this.closeResource(resultSet);
		}

		return list;
	}

	@Override
	public Integer queryOrderCount(Params params) {
		List<Object> paramsList = new ArrayList<Object>();
		Integer count = 0;
		StringBuffer sql = new StringBuffer(
				" select count(*) as count from `" + super.getTableName() + "`  where 1=1 ");

		Map<String, Object> make = super.getParamsList(params);
		sql.append(make.get("sql"));
		Object obj = make.get("paramsList");
		if (obj instanceof ArrayList<?>) {
			for (Object o : (List<?>) obj) {
				paramsList.add(Object.class.cast(o));
			}
		}

		ResultSet resultSet = this.executeQuery(sql.toString(), paramsList.toArray());
		try {
			while (resultSet.next()) {
				count = resultSet.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.closeResource();
			this.closeResource(resultSet);
		}

		return count;
	}
}

package com.crezyman.dao;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.crezyman.annotation.NotField;
import com.crezyman.annotation.Table;
import com.crezyman.utils.ClazzUtils;
import com.crezyman.utils.DataSourceUtil;
import com.crezyman.utils.EmptyUtils;
import com.crezyman.utils.Params;
import com.crezyman.utils.StringUtils;

public abstract class AbstractBaseDaoImpl implements IBaseDao {

	protected Connection connection;

	protected PreparedStatement pstm;

	public AbstractBaseDaoImpl(Connection connection) {
		this.connection = connection;
	}

	public ResultSet executeQuery(String sql, Object[] params) {
		ResultSet rs = null;
		try {
			pstm = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				pstm.setObject(i + 1, params[i]);
			}
			rs = pstm.executeQuery();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rs;
	}

	public int executeUpdate(String sql, Object[] params) {
		int updateRows = 0;
		try {
			pstm = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				pstm.setObject(i + 1, params[i]);
			}
			updateRows = pstm.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			updateRows = -1;
		}

		return updateRows;
	}

	public int executeInsert(String sql, Object[] params) {
		Long id = 0L;
		try {
			pstm = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < params.length; i++) {
				pstm.setObject(i + 1, params[i]);
			}

			pstm.executeUpdate();
			ResultSet rs = pstm.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getLong(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			id = null;
		}

		return id.intValue();
	}

	public boolean closeResource() {
		if (pstm != null) {
			try {
				pstm.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public boolean closeResource(ResultSet reSet) {
		if (reSet != null) {
			try {
				reSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public abstract Object tableToClass(ResultSet rs) throws Exception;

	public String getTableName() {
		// TODO Auto-generated method stub
		Class<?> clazz = this.getClass();
		if (clazz.isAnnotationPresent(Table.class)) {
			Table my = null;
			my = clazz.getAnnotation(Table.class);
			String tableName = my.name();
			return tableName;
		}
		return null;
	}

	public String genarateWhy(int num) {
		String s = "";
		for (int i = 0; i < num; i++) {
			s += "?";
			if (i != num - 1) {
				s += ",";
			}
		}
		return s;
	}

	public Map<String, Object> getParamsList(Params params) {
		StringBuffer sql = new StringBuffer("");
		List<Object> paramsList = new ArrayList<Object>();
		Map<String, Object> selectParams = params.getSelectParams();
		Map<String, Object> likeParams = params.getLikeParams();
		Map<String, Object> sortParams = params.getSortParams();
		Map<String, Object> updateParams = params.getUpdateParams();
		Map<String, List<Integer>> betweenParams = params.getBetweenParams();
		Map<String, List<Integer>> inParams = params.getInParams();

		boolean isOpenPage = params.isOpenPager();
		int startIndex = params.getStartIndex();
		int pageSize = params.getPageSize();

		Set<Entry<String, Object>> sel = selectParams.entrySet();
		Iterator<Entry<String, Object>> itr = sel.iterator();
		Set<Entry<String, Object>> lek = likeParams.entrySet();
		Iterator<Entry<String, Object>> itrLike = lek.iterator();
		Set<Entry<String, Object>> sort = sortParams.entrySet();
		Iterator<Entry<String, Object>> itrSort = sort.iterator();
		Set<Entry<String, Object>> update = updateParams.entrySet();
		Iterator<Entry<String, Object>> itrUpdate = update.iterator();
		Set<Entry<String, List<Integer>>> between = betweenParams.entrySet();
		Iterator<Entry<String, List<Integer>>> itrBetween = between.iterator();
		Set<Entry<String,List<Integer>>> in = inParams.entrySet();
		Iterator<Entry<String, List<Integer>>> itrIn = between.iterator();

		if (selectParams.size() > 0) {
			while (itr.hasNext()) {
				Entry<String, Object> keyOfValue = itr.next();
				if (EmptyUtils.isNotEmpty(keyOfValue.getKey())) {
					sql.append(" and " + keyOfValue.getKey() + " = ?");
					paramsList.add(keyOfValue.getValue());
				}
			}
		}

		if (likeParams.size() > 0) {
			while (itrLike.hasNext()) {
				Entry<String, Object> keyOfValue = itrLike.next();
				if (EmptyUtils.isNotEmpty(keyOfValue.getKey())) {
					sql.append(" and " + keyOfValue.getKey() + " like  ?");
					paramsList.add(keyOfValue.getValue());
				}
			}
		}

		if (between.size() > 0) {
			while (itrBetween.hasNext()) {
				Entry<String, List<Integer>> keyOfValue = itrBetween.next();
				if (EmptyUtils.isNotEmpty(keyOfValue.getKey())) {
					sql.append(" and " + keyOfValue.getKey() + " BETWEEN " + keyOfValue.getValue().get(0) + " and "
							+ keyOfValue.getValue().get(1));
				}
			}
		}
		
		if(in.size() > 0) {
			while(itrIn.hasNext()) {
				Entry<String, List<Integer>> keyOfValue = itrIn.next();
				if (EmptyUtils.isNotEmpty(keyOfValue.getKey())) {
					sql.append(" and ").append(keyOfValue.getKey()).append("(").append(keyOfValue.getValue().toString().substring(1, keyOfValue.getValue().toString().length() - 1)).append(")");
				}
			}
		}

		if (sortParams.size() > 0) {
			while (itrSort.hasNext()) {
				Entry<String, Object> keyOfValue = itrSort.next();
				if (EmptyUtils.isNotEmpty(keyOfValue.getKey())) {
					sql.append(" order by " + keyOfValue.getKey());

				}
			}
		}

		if (isOpenPage) {
			sql.append(" limit " + startIndex + ", " + pageSize);
		}

		if (update.size() > 0) {
			while (itrUpdate.hasNext()) {
				Entry<String, Object> keyOfValue = itrUpdate.next();
				if (EmptyUtils.isNotEmpty(keyOfValue.getKey())) {
					sql.append(" and  " + keyOfValue.getKey() + " = ? ");
					paramsList.add(keyOfValue.getValue());
				}
			}
		}

		Map<String, Object> rst = new HashMap<String, Object>();
		rst.put("paramsList", paramsList);
		rst.put("sql", sql.toString());
		return rst;
	}

	public Object orm(ResultSet rs, String[] fields) throws Exception {
		Class<?> clz = null;
		Object obj = null;
		Map<String, Object> result = getOrmTable(getTableName());
		obj = result.get("obj");
		clz = (Class<?>) result.get("clazz");

		Field[] fieldList = clz.getDeclaredFields();
		Method method = null;

		LinkedHashMap<String, String> allFieldsInfo = new LinkedHashMap<>();
		for (Field s : fieldList) {
			if (!s.isAnnotationPresent(NotField.class)) {
				allFieldsInfo.put(s.getName(), s.getType().toString());
			}
		}
		int index = 0;

		boolean bool = false;
		for (Map.Entry<String, String> entry : allFieldsInfo.entrySet()) {
			Class<?> clazz3 = getInstanceToClass(entry.getValue());
			Object objs = getInstanceToObject(clazz3.toString());
			if (objs instanceof String) {
				bool = StringUtils.isAcronym(entry.getKey());
				if (!bool) {
					method = clz.getMethod("set" + StringUtils.captureName(entry.getKey()), String.class);
				} else {
					method = clz.getMethod("set" + entry.getKey(), String.class);
				}
				method.invoke(obj, rs.getString(fields[index]));
			} else if (objs instanceof Integer) {
				bool = StringUtils.isAcronym(entry.getKey());
				if (!bool) {
					method = clz.getMethod("set" + StringUtils.captureName(entry.getKey()), Integer.class);
				} else {
					method = clz.getMethod("set" + entry.getKey(), Integer.class);
				}
				method.invoke(obj, rs.getInt(fields[index]));
			} else if (objs instanceof BigDecimal) {
				bool = StringUtils.isAcronym(entry.getKey());
				if (!bool) {
					method = clz.getMethod("set" + StringUtils.captureName(entry.getKey()), BigDecimal.class);
				} else {
					method = clz.getMethod("set" + entry.getKey(), BigDecimal.class);
				}
				method.invoke(obj, rs.getBigDecimal(fields[index]));
			} else if (objs instanceof Date) {
				bool = StringUtils.isAcronym(entry.getKey());
				if (!bool) {
					method = clz.getMethod("set" + StringUtils.captureName(entry.getKey()), Date.class);
				} else {
					method = clz.getMethod("set" + entry.getKey(), Date.class);
				}
				method.invoke(obj, rs.getDate(fields[index]));
			} else if (objs instanceof Double) {
				bool = StringUtils.isAcronym(entry.getKey());
				if (!bool) {
					method = clz.getMethod("set" + StringUtils.captureName(entry.getKey()), Double.class);
				} else {
					method = clz.getMethod("set" + entry.getKey(), Double.class);
				}
				method.invoke(obj, rs.getDouble(fields[index]));
			}
			index++;
		}
		return obj;
	}

	public static Map<String, Object> getOrmTable(String tableName) throws Exception {
		Map<String, Object> rst = new HashMap<>();
		String entityPackage = getDomainPackage("java1121.domain").trim();
		List<Class<?>> classes = ClazzUtils.getClasses(entityPackage);

		for (Class<?> class1 : classes) {
			if (class1.isAnnotationPresent(Table.class)) {
				Table t = class1.getAnnotation(Table.class);
				String name = t.name();
				if (name.equals(tableName)) {
					rst.put("obj", class1.newInstance());
					rst.put("clazz", class1);
				}
			}
		}
		return rst;
	}

	public static String getDomainPackage(String key) {
		Properties params = new Properties();
		String configFile = "application.properties";
		InputStream is = DataSourceUtil.class.getClassLoader().getResourceAsStream(configFile);
		try {
			params.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return params.getProperty(key);
	}

	public static Class<?> getInstanceToClass(String className) throws Exception {
		className = className.replace("class ", "");
		Class<?> clazz = Class.forName(className);
		Object r = null;
		if (className.contains("BigDecimal")) {
			Constructor<?>[] c = clazz.getConstructors();
			r = c[4].newInstance(0);
		} else if (className.contains("Integer")) {
			Constructor<?>[] c = clazz.getConstructors();
			r = c[0].newInstance(0);
		} else if (className.contains("Double")) {
			Constructor<?>[] c = clazz.getConstructors();
			r = c[0].newInstance(0);
		} else {
			r = clazz.newInstance();
		}
		return r.getClass();
	}

	public static Object getInstanceToObject(String className) throws Exception {
		className = className.replace("class ", "");
		Class<?> clazz = Class.forName(className);
		Object r = null;
		if (className.contains("BigDecimal")) {
			Constructor<?>[] c = clazz.getConstructors();
			r = c[4].newInstance(0);
		} else if (className.contains("Integer")) {
			Constructor<?>[] c = clazz.getConstructors();
			r = c[0].newInstance(0);
		} else if (className.contains("Double")) {
			Constructor<?>[] c = clazz.getConstructors();
			r = c[0].newInstance(0);
		} else {
			r = clazz.newInstance();
		}
		return r;
	}

	public List<String> getTableFields() throws Exception {
		String tbName = getTableName();
		Map<String, Object> map = getOrmTable(tbName);
		Class<?> clz = (Class<?>) map.get("clazz");
		Field[] fieldList = clz.getDeclaredFields();
		LinkedHashMap<String, String> allFieldsInfo = new LinkedHashMap<>();

		for (Field s : fieldList) {
			if (!s.isAnnotationPresent(NotField.class)) {
				allFieldsInfo.put(s.getName(), s.getType().toString());
			}
		}

		List<String> list = new ArrayList<>();
		for (Map.Entry<String, String> entry : allFieldsInfo.entrySet()) {
			String field = StringUtils.humpToLine2(entry.getKey());
			list.add(field);
		}

		List<String> rstList = new ArrayList<>();
		rstList.add(0, list.get(0));
		rstList.add(1, list.toString().substring(1, list.toString().length() - 1));
		list.remove(0);
		rstList.add(2, list.toString().substring(1, list.toString().length() - 1));

		StringBuffer sbf = new StringBuffer("");
		int i = 0;
		String s = null;
		for (Object o : list) {
			s = (String) o;
			sbf.append(s);
			if (i < list.size() - 1) {
				sbf.append("=?,");
			} else {
				sbf.append("=?");
			}
			i++;
		}
		rstList.add(3, sbf.toString());
		return rstList;
	}

}
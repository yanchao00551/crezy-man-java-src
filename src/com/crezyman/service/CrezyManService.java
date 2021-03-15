package com.crezyman.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.crezyman.dao.ModelDao;
import com.crezyman.utils.DataSourceUtil;
import com.crezyman.utils.Params;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
/**
 * 
 * @author 10947
 *
 * @param <K> 实体类
 * @param <V> Dao实现类
 */
@SuppressWarnings("finally")
public abstract class CrezyManService<T,V> implements IBaseService<T>{
	
	@Override
	public void delete(String id) {
		Connection connection=null;
		try {
			connection=DataSourceUtil.openConnection();
			ModelDao dao = (ModelDao) getActualTypeArgumentIns(connection);
			dao.deleteById(Integer.parseInt(id));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DataSourceUtil.closeConnection(connection);
		}
	}
	
	
	//获取泛型V类型字节码
	protected Object getActualTypeArgumentIns(Connection con) throws Exception{
		Type genType = (getClass().getGenericSuperclass());  
		Class<?> templatClazz = null;
		if(ParameterizedType.class.isInstance(genType)){  
			 ParameterizedType parameterizedType = (ParameterizedType) genType;
			 templatClazz = (Class<?>) parameterizedType.getActualTypeArguments()[1];
		}
		Constructor<?> constructor = templatClazz.getConstructor(Connection.class);
		return constructor.newInstance(con);
	}

	// 根据ID获取
	@SuppressWarnings("unchecked")
	@Override
	public T findById(String id) {
		T entity = null;
		Connection connection=null;
		try {
			connection=DataSourceUtil.openConnection();
			ModelDao dao = (ModelDao) getActualTypeArgumentIns(connection);
			entity = (T) dao.getEntityById(Integer.parseInt(id));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataSourceUtil.closeConnection(connection);
		}
		return entity;
	}

	// 保存
	public int save(Object entity) {
		Connection connection = null;
		int rst = 0;
		try {
			connection = DataSourceUtil.openConnection();
			ModelDao dao = (ModelDao) getActualTypeArgumentIns(connection);
			rst = dao.save(entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataSourceUtil.closeConnection(connection);
		}
		return rst;
	}


	// 更新
	public void update(Params params,Object entity) {
		Connection connection = null;
		try {
			connection = DataSourceUtil.openConnection();
			ModelDao dao  = (ModelDao) getActualTypeArgumentIns(connection);
			dao.update(params,entity);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataSourceUtil.closeConnection(connection);
		}
	}



	@Override
	public List<Object> queryOrdersList(Params params) {
		List<Object> list=new ArrayList<Object>();
		Connection connection = null;
		try {
			connection = DataSourceUtil.openConnection();
			ModelDao dao =(ModelDao) getActualTypeArgumentIns(connection);
			list=dao.queryEntityList(params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataSourceUtil.closeConnection(connection);
		}
		return list;
	}

	@Override
	public Integer queryCount(Params param) {
		Connection connection = null;
		Integer count=0;
		try {
			connection = DataSourceUtil.openConnection();
			ModelDao menuDao = (ModelDao) getActualTypeArgumentIns(connection);
			count=menuDao.queryOrderCount(param);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DataSourceUtil.closeConnection(connection);
			return count;
		}
	}
	
}

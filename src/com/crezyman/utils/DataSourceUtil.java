package com.crezyman.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.crezyman.dao.AbstractBaseDaoImpl;

import java.util.Properties;

/**
 *  升级了阿里的Dried数据库连接池
 * @author 10947
 *
 */
public class DataSourceUtil {
	private static DataSource dataSource;
	
	static {
		try {
			init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void init() throws Exception{
		try {
			Properties params=new Properties();
			String configFile = AbstractBaseDaoImpl.getDomainPackage("crezyman.databaseConfigFileName").trim();
			InputStream is=DataSourceUtil.class.getClassLoader().getResourceAsStream(configFile);
			params.load(is);
			dataSource = DruidDataSourceFactory.createDataSource(params);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}   

	public static Connection openConnection() throws SQLException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return connection;
	}

	public static void closeConnection(Connection connection) {
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

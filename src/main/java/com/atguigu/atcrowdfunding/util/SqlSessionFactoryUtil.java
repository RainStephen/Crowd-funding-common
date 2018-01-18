package com.atguigu.atcrowdfunding.util;
import java.io.IOException;
import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * 从mybatis的配置文件中获取sqlSessionFactory的工具类
 * 
 * @author Administrator
 *
 */
public class SqlSessionFactoryUtil {

	// mybatis的配置文件路径，类路径下直接写文件名
	private static String resource = "mybatis/mybatis-config.xml";

	private static SqlSessionFactory sqlSessionFactory;

	private SqlSessionFactoryUtil() {
		super();
	}

	public static SqlSessionFactory getSqlSessionFactory() {

		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(null == sqlSessionFactory){
			synchronized (SqlSessionFactoryUtil.class) {
				if(null == sqlSessionFactory){
					sqlSessionFactory = new SqlSessionFactoryBuilder()
					.build(inputStream);
				}
			}
		}
		 return sqlSessionFactory;
	}
	
	public static void main(String[] args) {
		SqlSessionFactory sqlSessionFactory2 = SqlSessionFactoryUtil
				.getSqlSessionFactory();
		System.out.println(sqlSessionFactory2);
	}

}

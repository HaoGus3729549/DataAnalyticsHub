package com.haogus.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;


public class MysqlUtil {

    public static SqlSession sqlSession;

    static {
        // 读取 MyBatis 配置文件
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 初始化 SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        // 获取 Configuration 对象，并设置开启驼峰命名对照
        Configuration configuration = sqlSessionFactory.getConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);

        // 获取 SqlSession
        sqlSession = sqlSessionFactory.openSession(true);
    }

    public static <T> T getDao(Class<T> clazz) {

        T mapper = sqlSession.getMapper(clazz);
        return mapper;
    }

    public void closeDao() {
        sqlSession.close();
    }
}

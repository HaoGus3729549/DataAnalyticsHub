package com.haogus;


import com.haogus.dao.UserDao;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class MyBatisExample {
    public static void main(String[] args) throws IOException {
        // 读取 MyBatis 配置文件
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");

        // 初始化 SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

        // 获取 SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();

        try {
            UserDao mapper = sqlSession.getMapper(UserDao.class);
            Object result = mapper.selectById(1);
            System.out.println(result);
        } finally {
            sqlSession.close();
        }
    }
}

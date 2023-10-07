package com.haogus.dao;

import com.haogus.entity.User;
import org.apache.ibatis.annotations.Param;


public interface UserDao {

    public Object selectById(@Param("id") Integer id);

    public User selectOneByUserName(@Param("userName") String userName, @Param("password") String password);

    int updateUserById(@Param("id") Integer id, @Param("firstName") String firstName, @Param("lastName") String lastName, @Param("userName") String userName, @Param("password") String password);

    int insert(@Param("user") User user);

    /**
     * 判断用户是否注册过
     *
     * @param userName
     * @return
     */
    int userIsExist(@Param("userName") String userName);

    void updateVip(@Param("id") Integer id);
}

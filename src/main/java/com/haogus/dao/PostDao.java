package com.haogus.dao;

import com.haogus.backend.Post;
import com.haogus.entity.User;
import org.apache.ibatis.annotations.Param;


public interface PostDao {


    void addPost(@Param("post") Post post);

    int existPost(@Param("postId") int postId);
}

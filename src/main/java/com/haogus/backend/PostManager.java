package com.haogus.backend;

import com.haogus.dao.PostDao;
import com.haogus.util.MysqlUtil;


public class PostManager {

    public static PostDao postDao = MysqlUtil.getDao(PostDao.class);

    public void addPost(Post post) {
        postDao.addPost(post);
    }

    public boolean existPost(int postId) {
        return postDao.existPost(postId) > 0;
    }
}

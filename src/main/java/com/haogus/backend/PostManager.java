package com.haogus.backend;

import com.haogus.dao.PostDao;
import com.haogus.util.MysqlUtil;

/**
 * @author guokang
 * @email guokang@tomonline-inc.com
 * @date 2023/10/8 9:39
 * @description
 */
public class PostManager {

    public static PostDao postDao = MysqlUtil.getDao(PostDao.class);

    public void addPost(Post post) {
        postDao.addPost(post);
    }

    public boolean existPost(int postId) {
        return postDao.existPost(postId) > 0;
    }
}

/**
 *
 */
package com.haogus.backend;

import java.util.List;
import java.util.Map;

public interface PostsInterface {

    // Add a post to the collection
    public boolean addPost(Post post);

    // Remove a post from the collection based on the post ID
    public String removePostByPostId(int postid);

    // Retrieve a post from the collection based on the post ID
    public String retrievePostByPostId(int postid);

    // Retrieve the top N posts with the most likes, and show retrieved posts in descending order of #likes
    public String retrievePostsByMostLike(int limit);


    public List<Post> getPosts();

    public Map<String, Integer> getShares();
}

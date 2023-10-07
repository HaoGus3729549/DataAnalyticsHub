package com.haogus.backend;

import java.util.*;
import java.util.Map.Entry;

public class Posts implements PostsInterface {

    /**
     *
     */
    ArrayList<Post> posts;
    int loopcount;
    String res;
    String result = null;
    int low = 1;
    int lower = 1;
    int lowest = 1;

    public Posts() {
        // constructor 
        posts = new ArrayList<Post>();
        loopcount = 0;
        res = "";
    }

    @Override
    public boolean addPost(Post post) {
        boolean result = false;
        if (posts.add(post)) result = true;
        else result = false;
        return result;
    }

    @Override
    public String removePostByPostId(int postid) {
        if (postid < 1) return "Error " + postid + " is not a valid integer!";
        String result = "";
        if (posts.size() < 1) {
            result = "Sorry the post does not exist in the database ";
        } else {
            for (Post post : posts) {
                if (post.getId() == postid) {
                    int postindex = posts.indexOf(post);
                    posts.remove(postindex);
                    result += "PostID " + postid + " Deleted Successfully \n";
                    break;
                } else {
                    result += "Sorry the post does not exist in the database ";
                }
            }
        }
        return result;
    }

    @Override
    public String retrievePostByPostId(int postid) {
        if (postid < 1) return "Error " + postid + " is not a valid integer!";
        if (posts.size() < 1) {
            result = "Sorry the post does not exist in the database ";
        } else {
            posts.forEach(post -> {
                if (post.getId() == postid) {
                    result = loopcount + ") " + post.getId() + "| " + post.getContent() + " | " + post.getLikes();
                } else {
                    result = "Sorry the post does not exist in the database ";
                }
            });
        }
        return result;
    }


    @SuppressWarnings("unchecked")
    @Override
    public String retrievePostsByMostLike(int limit) {
        if (limit < 1) return "Error " + limit + " is not a valid integer!";
        String result = "";
        if (posts.size() < 1) {
            result = "Sorry the post does not exist in the database ";
        } else {
            Map<Post, Integer> tempmap = new HashMap<Post, Integer>();
            result += "The top-" + limit + "posts with the most likes are: \n";
            posts.forEach(post -> {
                tempmap.put(post, post.getLikes());
            });
            List<Entry<Post, Integer>> templist = new ArrayList<>(tempmap.entrySet());
            templist.sort(Entry.comparingByValue(Comparator.reverseOrder()));
            loopcount = 1;
            for (Object n : templist) {

                if (loopcount == limit + 1) {
                    break;
                } else {
                    Post post = ((Entry<Post, Integer>) n).getKey();
                    result += loopcount + ") " + post.getId() + " | " + post.getContent() + " | " + post.getLikes() + "\n";

                }
                loopcount += 1;
            }
            templist.forEach(n -> {


            });
        }
        return result;
    }


    @Override
    public Map<String, Integer> getShares() {
        low = 1;
        lower = 1;
        lowest = 1;
        Map<String, Integer> shares = new HashMap<String, Integer>();

        posts.forEach(post -> {
            if (post.getShares() < 100) {
                lowest += 1;
            } else if (post.getShares() < 1000) {
                lower += 1;
            } else {
                low += 1;
            }
        });

        shares.put("lowest", lowest);
        shares.put("lower", lower);
        shares.put("low", low);
        return shares;

    }

    @Override

    public ArrayList<Post> getPosts() {
        return this.posts;
    }

}

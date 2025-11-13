package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Post;


public interface PostRepository extends JpaRepository<Post, Long> {


    public Post findByPostID(Long postID);

    @Query(value = "SELECT * FROM post_data where posted_by_userid = ?1 ORDER BY postid DESC",nativeQuery = true)
    public Page<Post> findByUserID(Long userID,Pageable pageable);

    @Query(value = "SELECT FLOOR(COUNT(postid)/10) FROM post_data where posted_by_userid = ?1 GROUP BY postid ORDER BY postid DESC",nativeQuery = true)
    public int getUserPages(Long userID);

    @Query(value = "SELECT FLOOR(COUNT(postid)/10) FROM post_data GROUP BY postid ORDER BY postid DESC",nativeQuery = true)
    public int getOverallPages();


}

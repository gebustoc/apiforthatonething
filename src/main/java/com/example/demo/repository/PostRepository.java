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

    @Query(value = "SELECT ROUND(COUNT(*)/10) FROM post_data where posted_by_userid = ?1",nativeQuery = true)
    public Integer getUserPages(Long userID);

    @Query(value = "SELECT ROUND(COUNT(*)/10) FROM post_data",nativeQuery = true)
    public Integer getOverallPages();


}

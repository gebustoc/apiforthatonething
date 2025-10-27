package com.example.demo.repository;

import com.example.demo.model.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PostRepository extends JpaRepository<Post, Long> {


    public Post findByPostID(Long postID);

    @Query(value = "SELECT * FROM post_data where userid = ?1 ORDER BY postid DESC",nativeQuery = true)
    public Page<Post> findByUserID(Long userID,Pageable pageable);

    @Query(value = "SELECT FLOOR(COUNT(postid)/5) FROM post_data where userid = ?1 ORDER BY postid DESC",nativeQuery = true)
    public int getUserPages(Long userID);

    @Query(value = "SELECT FLOOR(COUNT(postid)/5) FROM post_data ORDER BY postid DESC",nativeQuery = true)
    public int getOverallPages();


}

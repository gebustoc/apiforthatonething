package com.example.demo.repository;

import com.example.demo.model.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PostRepository extends JpaRepository<Post, Long> {

    public Post findByPostID(Long postID);

    @Query(value = "SELECT * FROM post_data where userid = ?1",nativeQuery = true)
    public Page<Post> findByUserID(Long userID,Pageable pageable);
}

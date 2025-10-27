package com.example.demo.service;


import com.example.demo.model.AppUser;
import com.example.demo.model.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public Post findByPostID(Long postID){return postRepository.findByPostID(postID);}
    

    public List<Post> findByUserIDPaginated(Long userID, int page){
        Pageable paige = PageRequest.of(page, 65536,Sort.by("postID").descending());
        return postRepository.findByUserID(userID,paige).getContent();
    }

    public List<Post> getPage(int page){
        Pageable paige = PageRequest.of(page, 65536,Sort.by("postID").descending());
        return postRepository.findAll(paige).getContent();
    }

    public Post savePost(Post post){return postRepository.save(post);}
    
    public List<Post> stripSensitiveInfo(List<Post> posts,AppUserService strippingService){
        for (int i = 0; i < posts.size(); i++) {
            posts.get(i).setPostedBy(strippingService.stripSensitiveInfo(posts.get(i).getPostedBy()));
        }        
        return posts;
    }

    public int getUserPages(Long userID){return postRepository.getUserPages(userID);}
    public int getOverallPages(){return postRepository.getOverallPages();}
}

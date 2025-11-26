package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.model.Post;
import com.example.demo.service.AppUserService;
import com.example.demo.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // TODO:fix later
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    @Autowired
    private PostService service;
    @Autowired 
    private AppUserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<Post> getByPostID(@PathVariable Long id ){
        Post post = service.findByPostID(id);

        if (post == null){return ResponseEntity.notFound().build();}
        post.setPostedBy(userService.stripSensitiveInfo(post.getPostedBy()));
        return ResponseEntity.ok(post);
    }
    

    @GetMapping("page/{pageNumber}")
    public ResponseEntity<List<Post>> getPage(@PathVariable int pageNumber){
        List<Post> paige = service.getPage(pageNumber);
        //if (paige.isEmpty()) return ResponseEntity.noContent().build();
        service.stripSensitiveInfo(paige, userService);        
        return ResponseEntity.ok(paige);
    }

    // TODO: fix this (i don't actually know how)
    @GetMapping("user/{userID}")
    public ResponseEntity<List<Post>> findByUserIDPaginated(@PathVariable Long userID, @RequestParam int pageNumber){

        List<Post> paige = service.findByUserIDPaginated(userID,pageNumber);
        //if (paige.isEmpty()) return ResponseEntity.noContent();
        service.stripSensitiveInfo(paige, userService);
        return ResponseEntity.ok(paige);
    }
    @GetMapping("pagecount/{userID}")
    public ResponseEntity<Integer> getUserPages(@PathVariable  Long userID){
        return ResponseEntity.ok(service.getUserPages(userID));
    }
    @GetMapping("pagecount")
    public ResponseEntity<Integer> getOverallPages(){
        return ResponseEntity.ok(service.getOverallPages());
    }


    @PostMapping("/upload")
    public ResponseEntity<String> UploadPost(@RequestParam MultipartFile file, @RequestParam String postData) throws JsonMappingException, JsonProcessingException {
        Post post;
        ObjectMapper objectMapper = new ObjectMapper();
        try{
            post = objectMapper.readValue(postData,Post.class);
        } catch(Exception e){
            return ResponseEntity.badRequest().body("Error Reading Post Data");
        }
        if (post == null) return ResponseEntity.badRequest().build();
        if (userService.login(post.getPostedBy()) == null)return ResponseEntity.badRequest().body("post has no user.");
        post.setPostID(null); // quick hack to avoid fuckery

        if (post.getPostTitle().length() >= 32) return  ResponseEntity.badRequest().body("exceeds post title limits");
        if (post.getPostDescription().length() >= 512) return  ResponseEntity.badRequest().body("exceeds description limits");



        try {
            post.setFileExtension("webp"); // still stores extension because backwards compatiblity oops
            post = service.savePost(post);
            saveImage(file,post.getPostID());
            return ResponseEntity.status(201).body("Post uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }

    }


    private String extractExtension(String filename){
        int filenameIdx = filename.lastIndexOf(".");
        if (filenameIdx > 0)return filename.substring(filenameIdx+1);
        return "";
    }


    private String saveImage(MultipartFile file,Long postID) throws IOException {
        Path uploadPath = Paths.get("images");
        if (!Files.exists(uploadPath)) {Files.createDirectories(uploadPath);}
        Path filePath = uploadPath.resolve(postID.toString()+"."+"webp");
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }






    

}

package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Post;
import com.example.demo.service.AppUserService;
import com.example.demo.service.PostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


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
        System.out.println(postData);
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



        String bbID = saveImage(file);
        if (bbID == null)return ResponseEntity.status(424).body("Could not upload to ImageBB");
        post.setImageSource(bbID);
        post = service.savePost(post);
        return ResponseEntity.status(201).body("Post uploaded successfully");
    
    }


    private String extractExtension(String filename){
        int filenameIdx = filename.lastIndexOf(".");
        if (filenameIdx > 0)return filename.substring(filenameIdx+1);
        return "";
    }

    
    private String saveImage(MultipartFile file) {
        String bbURL = "https://api.imgbb.com/1/upload?key="+System.getenv("BB_KEY");


        try {
            HttpHeaders headers = new HttpHeaders();
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);    
            Resource imageRes = new ByteArrayResource(file.getBytes()) {
                @Override
                // This is crucial: the filename must be provided for proper multipart handling
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            

            body.add("image", imageRes);
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(bbURL, requestEntity, String.class);
            System.out.println(response.getBody());
            JSONObject responseData = new JSONObject(response.getBody());
        
            return responseData.getJSONObject("data").getString("display_url");

		} catch (Exception e) {
			e.printStackTrace();
		}



       return null;
    }






    

}

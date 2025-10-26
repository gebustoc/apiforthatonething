package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // TODO:fix later

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/users")
public class AppUserController {
    @Autowired
    private AppUserService service;


    @PutMapping("/login")
    public ResponseEntity<AppUser> login(@RequestBody AppUser credentials){
        AppUser loggedUser = service.login(credentials);
        if (loggedUser != null) return ResponseEntity.ok(loggedUser);
        return ResponseEntity.status(409).build();
    }

    private boolean userExists(AppUser user){
        return (service.findByEmail(user.getEmail()) != null) || (service.findByUserID(user.getUserID()) != null);
    }

    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody AppUser newUser){
        newUser.setUserID(null); // quick hack to avoid crashing when trying to create an user from the app
        if (!userExists(newUser)){
            return ResponseEntity.status(201).body(service.registerUser(newUser));
        }
        return ResponseEntity.status(409).build(); // i still dk if this is the proper one, well
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUser> getByUserID(@PathVariable Long id ){
        AppUser user = service.stripSensitiveInfo(service.findByUserID(id));
        if (user == null){return ResponseEntity.notFound().build();}
        return ResponseEntity.ok(user);
    }



}

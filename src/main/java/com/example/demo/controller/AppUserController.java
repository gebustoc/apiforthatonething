package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // TODO:fix later

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/users")
public class AppUserController {
    @Autowired
    private AppUserService service;
    private final String emailRegex = "[a-z0-9]+[_a-z0-9\\.-]*[a-z0-9]+@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})";
    final Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);


    @PutMapping("/login")
    public ResponseEntity<AppUser> login(@RequestBody AppUser credentials){
        AppUser loggedUser = service.login(credentials);
        if (loggedUser != null) return ResponseEntity.ok(loggedUser);
        return ResponseEntity.status(401).build();
    }

    private boolean userExists(AppUser user){
        return (service.findByEmail(user.getEmail()) != null) || (service.findByUserID(user.getUserID()) != null);
    }

    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody AppUser newUser){
        if (newUser.getUserName().length() >= 32) return  ResponseEntity.badRequest().body(new AppUser(null,"Exceeds Username Character limits",null,null));
        if (newUser.getEmail().length() >= 255) return  ResponseEntity.badRequest().body(new AppUser(null,null,"Exceeds Email Character limits",null));
        if (newUser.getPasswordHash().length() >= 255) return  ResponseEntity.badRequest().body(new AppUser(null,null,null,"Exceeds Password Character limits"));

        Matcher matcher = pattern.matcher(newUser.getEmail());
        if (!matcher.matches()) return  ResponseEntity.badRequest().body(new AppUser(null,null,"Email is not valid",null));


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

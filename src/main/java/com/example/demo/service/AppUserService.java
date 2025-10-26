package com.example.demo.service;


import com.example.demo.model.AppUser;
import com.example.demo.repository.AppUserRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AppUserService {
    
    //private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    @Autowired
    private AppUserRepository appUserRepository;

    public AppUser findByUserName(String userName){return appUserRepository.findByUserName(userName);}
    public AppUser findByEmail(String email){return appUserRepository.findByEmail(email);}
    public AppUser findByUserID(Long userID){return appUserRepository.findByUserID(userID);}
    public AppUser registerUser(AppUser appUser){
        appUser.setUserID(null);
        return appUserRepository.save(appUser);
    }
    

    public AppUser login(AppUser credentials){
        if (credentials == null) return null;
        AppUser checkedUser = findByUserID(credentials.getUserID());
        if (checkedUser == null) checkedUser = findByEmail(credentials.getEmail());
        if (checkedUser == null) return null;
        //if (passwordEncoder.matches(credentials.getPasswordHash(), checkedUser.getPasswordHash())) return checkedUser;
        if (credentials.getPasswordHash().equals(checkedUser.getPasswordHash())) return checkedUser;
        return null;
    }

    // this is meant to strip info from OTHER users that aren't the
    // main user who is currently using the software
    public AppUser stripSensitiveInfo(AppUser user){
        if (user == null){return null;}
        user = user.duplicate();
        user.setPasswordHash("");
        user.setEmail("");
        return user;
    }
    // this is meant to strip info from OTHER users that aren't the
    // main user who is currently using the software
    public List<AppUser> stripSensitiveInfo(List<AppUser> users){
        for (int i = 0; i < users.size(); i++) {
            users.set(i, stripSensitiveInfo(users.get(i)));
        }        
        return users;
    }
    

}

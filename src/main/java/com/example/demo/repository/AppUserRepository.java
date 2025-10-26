package com.example.demo.repository;

import com.example.demo.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    public AppUser findByUserName(String userName);
    public AppUser findByEmail(String email);
    public AppUser findByUserID(Long userID);

}

package com.example.demo.model;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="app_user")
@Data
@ToString
public class AppUser {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long userID;

    @Column(unique = true,length = 32)
    private String userName;
    @Column(unique = true)
    private String email;

    @Column
    private String passwordHash;

    public AppUser duplicate(){return new AppUser(userID,userName,email,passwordHash);}



}

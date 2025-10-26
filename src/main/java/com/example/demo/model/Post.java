package com.example.demo.model;


import org.antlr.v4.runtime.misc.NotNull;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@Table(name="post_data")
public class Post {


    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long postID;

    @Column(length=32,nullable=false)
    private String postTitle;

    @Column(length=512,nullable=false)
    private String postDescription;

    @ManyToOne
    @JoinColumn(name = "userID", nullable = false)
    private AppUser postedBy;

    @NotNull
    @Column(length = 8)
    private String fileExtension;
    
}

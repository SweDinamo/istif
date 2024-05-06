package com.istif_backend.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.istif_backend.model.Istif;
import com.istif_backend.model.User;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class LoginResponse {
    private Long id;
    private String email;

    private String username;

    private String profilePhoto;

    private String biography;


    @JsonIncludeProperties({"id","title"})
    private List<Istif> stories;

    @JsonIgnore
    private Set<Long> likedIstifs;

    @JsonIncludeProperties({"id","username"})
    private Set<User> followers;


    @JsonIncludeProperties({"id","username"})
    private Set<User> following;

    private String token;

    public LoginResponse(User user,String token) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.profilePhoto = user.getProfilePhoto();
        this.biography = user.getBiography();
        this.followers = user.getFollowers();
        this.following = user.getFollowing();
        this.token = token;
    }
}

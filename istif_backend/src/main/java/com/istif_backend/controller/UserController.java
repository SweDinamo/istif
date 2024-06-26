package com.istif_backend.controller;

import com.istif_backend.model.User;
import com.istif_backend.request.FollowRequest;
import com.istif_backend.request.LoginRequest;
import com.istif_backend.request.RegisterRequest;
import com.istif_backend.request.UserUpdateRequest;
import com.istif_backend.response.LoginResponse;
import com.istif_backend.service.ImageService;
import com.istif_backend.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;



    @GetMapping("/test")
    public String helloWorld(){
        return "<h1>Hello world!</h1>";
    }



    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, HttpServletRequest request) throws AccountNotFoundException {
        User user = userService.findByUserId(id);
        if(user != null){
            return ResponseEntity.ok(user);
        }
        else{
            throw new AccountNotFoundException();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
                                   HttpServletResponse response) throws AccountNotFoundException {
        User foundUser = userService.findByIdentifierAndPassword(loginRequest.getIdentifier(), loginRequest.getPassword());
        String token = userService.generateUserToken(foundUser);
        Cookie cookie = new Cookie("Bearer", token);
        cookie.setPath("/api");
        response.addCookie(cookie);
        foundUser.setProfilePhoto(null);
        return ResponseEntity.ok(new LoginResponse(foundUser,token));
    }


    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Bearer", null);
        cookie.setPath("/api");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok("Logged out");
    }



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        User newUser = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .build();
        User registeredUser = userService.addUser(newUser);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateRequest updateRequest, HttpServletRequest request){
        User user = userService.validateTokenizedUser(request);
        return ResponseEntity.ok(userService.updateUser(user,updateRequest));
    }

    @PostMapping(value= "/photo", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadPhoto(@RequestParam("photo") MultipartFile file, HttpServletRequest request) throws Exception{
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload!");
        }
        try {
            User foundUser = userService.validateTokenizedUser(request);
            return ResponseEntity.ok(userService.updateUserPhoto(foundUser,file));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/all")
    public ResponseEntity<?> findAllUsers(){
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping("/follow")
    public ResponseEntity<?> followUser(@RequestBody FollowRequest followRequest, HttpServletRequest request){
        User foundUser = userService.validateTokenizedUser(request);
        return ResponseEntity.ok(userService.followUser(foundUser, followRequest.getUserId()));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> showUserProfile(HttpServletRequest request){
        return ResponseEntity.ok(userService.validateTokenizedUser(request));
    }
}

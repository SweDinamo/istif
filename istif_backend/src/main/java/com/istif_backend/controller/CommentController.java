package com.istif_backend.controller;

import com.istif_backend.model.Istif;
import com.istif_backend.model.User;
import com.istif_backend.request.CommentRequest;
import com.istif_backend.request.LikeRequest;
import com.istif_backend.service.CommentService;
import com.istif_backend.service.IstifService;
import com.istif_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private UserService userService;

    @Autowired
    private IstifService istifService;

    @Autowired
    private CommentService commentService;


    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody CommentRequest commentRequest, HttpServletRequest request){
        User user = userService.validateTokenizedUser(request);
        Istif istif = istifService.getIstifByIstifId(commentRequest.getIstifId());
        return ResponseEntity.ok(commentService.createComment(commentRequest,user,istif));
    }
    @PostMapping("/like")
    public ResponseEntity<?> likeComment(@RequestBody LikeRequest likeRequest, HttpServletRequest request){
        User tokenizedUser = userService.validateTokenizedUser(request);

        return ResponseEntity.ok(commentService.likeComment(likeRequest.getLikedEntityId(),tokenizedUser.getId()));
    }
}

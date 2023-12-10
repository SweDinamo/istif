package com.istif_backend.controller;

import com.istif_backend.model.Comment;
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

import java.util.List;

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

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getByCommentId(@PathVariable Long commentId,HttpServletRequest request){
        Comment foundComment = commentService.getCommentById(commentId);
        if (foundComment!=null) {
            return ResponseEntity.ok(foundComment);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/byIstif/{istifId}")
    public ResponseEntity<?> getCommentsByIstifId(@PathVariable Long istifId,HttpServletRequest request){
        List<Comment> foundComments = commentService.getCommentsByIstifId(istifId);
        if (foundComments!=null) {
            return ResponseEntity.ok(foundComments);
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteByCommentId(@PathVariable Long commentId,HttpServletRequest request){
        Comment foundComment = commentService.getCommentById(commentId);
        String deletionStatus = commentService.deleteComment(foundComment);
        return ResponseEntity.ok(deletionStatus);
    }
}

package com.istif_backend.service;

import com.istif_backend.model.Comment;
import com.istif_backend.model.Istif;
import com.istif_backend.model.User;
import com.istif_backend.repository.CommentRepository;
import com.istif_backend.request.CommentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    public Comment createComment(CommentRequest commentRequest, User user, Istif istif) {
        Comment comment = Comment.builder()
                .text(commentRequest.getCommentText())
                .istif(istif)
                .user(user)
                .likes(new HashSet<>())
                .build();
        return commentRepository.save(comment);
    }

    public Comment likeComment(Long commentId, Long userId) {
        Comment comment = getCommentByCommentId(commentId);
        if (comment.getLikes().contains(userId)) {
            comment.getLikes().remove(userId);
        }
        else{
            comment.getLikes().add(userId);
        }
        return commentRepository.save(comment);
    }

    public Comment getCommentByCommentId(Long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new NoSuchElementException("Comment with id '" + commentId + "' not found");
        }
        return optionalComment.get();
    }

    public void deleteComment(Comment comment){
        commentRepository.delete(comment);
    }
}

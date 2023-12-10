package com.istif_backend.repository;

import com.istif_backend.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment getCommentById(Long commentId);

    List<Comment> findAllByIstifId(Long storyId);
}

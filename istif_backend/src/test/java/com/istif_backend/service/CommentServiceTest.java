package com.istif_backend.service;

import com.istif_backend.model.Comment;
import com.istif_backend.model.Istif;
import com.istif_backend.model.User;
import com.istif_backend.repository.CommentRepository;
import com.istif_backend.request.CommentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateComment() {
        CommentRequest request = new CommentRequest();
        User user = new User();
        Istif istif = new Istif();
        Comment mockComment = new Comment();

        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        Comment createdComment = commentService.createComment(request, user, istif);

        assertNotNull(createdComment);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void testLikeComment() {
        Long commentId = 1L;
        Long userId = 1L;
        Comment mockComment = new Comment();
        mockComment.setLikes(new HashSet<>());

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        Comment updatedComment = commentService.likeComment(commentId, userId);

        assertTrue(updatedComment.getLikes().contains(userId));
        verify(commentRepository, times(1)).save(mockComment);
    }

    @Test
    public void testGetCommentsByIstifId() {
        Long istifId = 1L;
        List<Comment> mockComments = Arrays.asList(new Comment(), new Comment());

        when(commentRepository.findAllByIstifId(istifId)).thenReturn(mockComments);

        List<Comment> comments = commentService.getCommentsByIstifId(istifId);

        assertNotNull(comments);
        assertEquals(2, comments.size());
        verify(commentRepository, times(1)).findAllByIstifId(istifId);
    }

    @Test
    public void testGetCommentByCommentId() {
        Long commentId = 1L;
        Comment mockComment = new Comment();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(mockComment));

        Comment comment = commentService.getCommentByCommentId(commentId);

        assertNotNull(comment);
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    public void testGetCommentByCommentId_NotFound() {
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            commentService.getCommentByCommentId(commentId);
        });
    }

    @Test
    public void testDeleteComment() {
        Comment mockComment = new Comment();
        mockComment.setId(1L);

        doNothing().when(commentRepository).delete(mockComment);
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.empty());

        String result = commentService.deleteComment(mockComment);

        assertEquals("comment deleted", result);
        verify(commentRepository, times(1)).delete(mockComment);
    }




}
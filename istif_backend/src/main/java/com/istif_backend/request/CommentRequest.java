package com.istif_backend.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {

    private String commentText;
    private Long storyId;
}

package com.istif_backend.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.istif_backend.configuration.LocalDateParser;
import com.istif_backend.model.Comment;
import com.istif_backend.model.Istif;
import com.istif_backend.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SingleIstifResponse{
    private Long id;
    private String text;
    private String title;
    private String titleLink;
    private String source;
    private List<String> labels;

    @JsonIncludeProperties(value = {"id" , "username"})
    private User user;

    private List<Comment> comments;

    private Integer likeSize;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "Europe/Istanbul")
    private Date createdAt;

    private String istifDate;

    public SingleIstifResponse(Istif istif) {
        this.id = istif.getId();
        this.text = istif.getText();
        this.title = istif.getTitle();
        this.titleLink = istif.getTitleLink();
        this.source = istif.getSource();
        this.labels = istif.getLabels();
        this.user = istif.getUser();
        this.comments = istif.getComments();
        this.likeSize = istif.getLikes().size();
        this.createdAt = istif.getCreatedAt();
        this.istifDate = LocalDateParser.localDateToString(istif.getRelevantDate(),istif.getDateFlag());
    }
}

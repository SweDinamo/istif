package com.istif_backend.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.istif_backend.configuration.LocalDateParser;
import com.istif_backend.model.Istif;
import com.istif_backend.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class IstifListResponse{
    private Long id;
    private String title;
    private String titleLink;
    private String source;
    private String text;
    private List<String> labels;

    @JsonIncludeProperties(value = {"id" , "username"})
    private User user;

    private Integer likeSize;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "Europe/Istanbul")
    private Date createdAt;

    private String istifDate;

    public IstifListResponse(Istif istif) {
        this.id = istif.getId();
        this.title = istif.getTitle();
        this.text = istif.getText();
        this.titleLink = istif.getTitleLink();
        this.source = istif.getSource();
        this.labels = istif.getLabels();
        this.user = istif.getUser();
        this.likeSize = istif.getLikes().size();
        this.createdAt = istif.getCreatedAt();
        this.istifDate = LocalDateParser.localDateToString(istif.getRelevantDate(),istif.getDateFlag());
    }

    public IstifListResponse() {
        this.title = "No istif found!";
    }
}

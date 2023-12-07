package com.istif_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.*;

@Entity
@Table(name="stories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Istif extends BaseEntity{


    @NotBlank
    @Lob
    private String text;


    private String title;

    @NotBlank
    private String titleLink;

    @ElementCollection
    @Column
    private List<String> labels = new ArrayList<>();

    @JsonIncludeProperties(value = {"id" , "username"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "istif")
    private List<Comment> comments;

    @Column(name = "likes")
    private Set<Long> likes = new HashSet<>();

    @Column(name = "created_at")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "Europe/Istanbul")
    private Date createdAt  = new Date();

    @Column(name = "relevant_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date relevantDate;

    private Integer shareFlag = -1;

    @JsonIgnore
    public int getLikesSize(){
        return likes.size();
    }

}

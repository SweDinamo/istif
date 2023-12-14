package com.istif_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
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

    private String source;

    @Column(name = "edited_at")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm", timezone = "Europe/Istanbul")
    private Date editedAt;

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

    @Column(name = "relevant_date")
    private LocalDate relevantDate;

    private Integer DateFlag = -1; // 3 for dd/mm/yyyy, 2 for mm/yyyy, 1 for yyyy, -1 for default

    private Integer shareFlag = -1; // 0 if private, 1 if public

    @JsonIgnore
    public int getLikesSize(){
        return likes.size();
    }

}

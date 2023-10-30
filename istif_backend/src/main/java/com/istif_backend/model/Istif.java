package com.istif_backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @NotBlank
    private String title;

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
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "UTC")
    private Date createdAt;

}

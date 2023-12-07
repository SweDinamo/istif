package com.istif_backend.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
public class IstifCreateRequest {
    private String text;
    private String titleLink;
    private String title;
    private ArrayList<String> labels = new ArrayList<>();
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date relevantDate;
    private Integer shareFlag;
}

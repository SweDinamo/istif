package com.istif_backend.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
public class IstifEditRequest {
    private String text;

    private String title;

    private String titleLink;


    private String header;

    private ArrayList<String> labels = new ArrayList<>();

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date relevantDate;

    private Integer shareFlag;
}

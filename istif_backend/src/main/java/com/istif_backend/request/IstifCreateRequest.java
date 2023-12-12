package com.istif_backend.request;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class IstifCreateRequest {
    private String text;
    private String titleLink;
    private String title;
    private String source;
    private ArrayList<String> labels = new ArrayList<>();
    private String relevantDate;
    private Integer shareFlag;
}

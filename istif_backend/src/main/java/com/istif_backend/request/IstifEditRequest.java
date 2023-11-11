package com.istif_backend.request;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class IstifEditRequest {
    private String text;

    private String title;

    private ArrayList<String> labels = new ArrayList<>();

    private Integer shareFlag;
}

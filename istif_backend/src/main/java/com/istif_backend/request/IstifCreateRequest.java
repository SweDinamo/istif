package com.istif_backend.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

@Getter
@Setter
public class IstifCreateRequest {
    private String text;
    private String title;
    private ArrayList<String> labels = new ArrayList<>();

    private LocalDate relevantDate;

    private Integer shareFlag;
}

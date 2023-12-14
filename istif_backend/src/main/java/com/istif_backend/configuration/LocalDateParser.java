package com.istif_backend.configuration;

import com.istif_backend.model.Istif;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class LocalDateParser {

    public static Istif parseDate(String dateString, Istif istif) {

        DateTimeFormatter formatterFull = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            if(dateString == null || istif.getRelevantDate() != null){
                return istif;
            }
            istif.setRelevantDate(LocalDate.parse(dateString, formatterFull));
            istif.setDateFlag(3);
            return istif;
        } catch (DateTimeParseException e1) {
            try {
                istif.setRelevantDate(LocalDate.parse("01/" + dateString, formatterFull));
                istif.setDateFlag(2);
                return istif;
            } catch (DateTimeParseException e2) {
                try {
                    istif.setRelevantDate(LocalDate.parse("01/01/" + dateString, formatterFull));
                    istif.setDateFlag(1);
                    return istif;
                } catch (DateTimeParseException e3) {
                    throw new IllegalArgumentException("Invalid date format: " + dateString);
                }
            }
        }
    }

    public static String localDateToString(LocalDate date, Integer dateFlag) {
        if (date == null) {
            return null;
        }

        switch (dateFlag) {
            case 3:
                return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            case 2:
                return date.format(DateTimeFormatter.ofPattern("MM/yyyy"));
            case 1:
                return Integer.toString(date.getYear());
            default:
                throw new IllegalArgumentException("Unknown DateFlag");
        }

    }

    public static LocalDate convertDateToLocalDate(Date date) {
        if (date == null) {
            return null;
        }

        // Convert Date to LocalDate
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}


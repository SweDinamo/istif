package com.istif_backend.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getText();
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }

        LocalDate parsedDate = tryParse(dateString, "dd/MM/yyyy");
        if (parsedDate != null) {
            return parsedDate;
        }

        parsedDate = tryParse("01/" + dateString, "MM/yyyy");
        if (parsedDate != null) {
            return parsedDate;
        }

        return tryParse("01/01/" + dateString, "yyyy");
    }

    private LocalDate tryParse(String dateString, String format) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDate.parse(dateString, formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}


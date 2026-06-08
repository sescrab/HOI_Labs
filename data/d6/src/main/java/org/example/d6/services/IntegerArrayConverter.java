package org.example.d6.services;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class IntegerArrayConverter implements AttributeConverter<Integer[], String> {

    @Override
    public String convertToDatabaseColumn(Integer[] attribute) {
        if (attribute == null) return null;
        return "{" + Arrays.stream(attribute).map(String::valueOf).collect(Collectors.joining(",")) + "}";
    }

    @Override
    public Integer[] convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String cleaned = dbData.replaceAll("[{}]", "");
        if (cleaned.isEmpty()) return new Integer[0];
        String[] parts = cleaned.split(",");
        return Arrays.stream(parts).map(Integer::valueOf).toArray(Integer[]::new);
    }
}
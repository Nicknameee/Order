package io.management.ua.products.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class ListArrayConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "{}";
        }
        return "{" + attribute.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",")) + "}";
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty() || "{}".equals(dbData.trim())) {
            return new ArrayList<>();
        }
        String trimmedData = dbData.trim();
        if (!trimmedData.startsWith("{") || !trimmedData.endsWith("}")) {
            throw new IllegalArgumentException("Invalid array format: " + dbData);
        }
        String[] elements = trimmedData.substring(1, trimmedData.length() - 1).split(",");
        return Arrays.stream(elements)
                .map(s -> s.replaceAll("^\"|\"$", ""))
                .collect(Collectors.toList());
    }
}
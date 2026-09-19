package com.twb.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** List&lt;String&gt; 与逗号分隔字符串互转（用于任务标签等） */
@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join(",", list);
    }

    @Override
    public List<String> convertToEntityAttribute(String db) {
        if (db == null || db.trim().isEmpty()) return new ArrayList<>();
        return Arrays.stream(db.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }
}

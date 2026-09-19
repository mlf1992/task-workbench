package com.twb.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** List&lt;Long&gt; 与逗号分隔字符串互转（用于标签关联任务ID） */
@Converter
public class LongListConverter implements AttributeConverter<List<Long>, String> {
    @Override
    public String convertToDatabaseColumn(List<Long> list) {
        if (list == null || list.isEmpty()) return "";
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    @Override
    public List<Long> convertToEntityAttribute(String db) {
        if (db == null || db.trim().isEmpty()) return new ArrayList<>();
        return Arrays.stream(db.split(",")).map(String::trim).filter(s -> !s.isEmpty())
                .map(Long::valueOf).collect(Collectors.toList());
    }
}

package com.twb.service;

import com.twb.entity.Activity;
import com.twb.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** 操作动态记录服务（历史记录页数据来源） */
@Service
public class ActivityService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public void log(String type, String text, String operator) {
        Activity a = new Activity();
        a.setType(type == null ? "system" : type);
        a.setText(text);
        a.setOperator(operator == null || operator.isEmpty() ? "我" : operator);
        a.setTime(LocalDateTime.now().format(FMT));
        activityRepository.save(a);
    }
}

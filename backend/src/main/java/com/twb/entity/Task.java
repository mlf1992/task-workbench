package com.twb.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** 任务（任务管理页主表） */
@Data
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String priority;   // high / mid / low
    private String deadline;   // 展示文案：今天 10:00 / 9月17日
    private Integer sortTs;    // 排序值（越小越靠前）
    private String status;     // pending/doing/waiting/blocked/done
    private String owner;
    private String color;      // 头像色 c-blue ...
    private String next;       // 下一步
    private String project;    // 所属项目名
    @Convert(converter = StringListConverter.class)
    @Column(length = 200)
    private List<String> tags = new ArrayList<>(); // today 等
    private Boolean urgent = false;
}

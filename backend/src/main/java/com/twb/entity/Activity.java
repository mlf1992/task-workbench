package com.twb.entity;

import lombok.Data;

import javax.persistence.*;

/** 操作动态（历史记录） */
@Data
@Entity
@Table(name = "activity")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String time;     // 2026-09-18 10:22
    private String type;     // task/project/event/waiting/file/meeting/tag/system
    private String text;     // 动态内容
    private String operator; // 操作人
}

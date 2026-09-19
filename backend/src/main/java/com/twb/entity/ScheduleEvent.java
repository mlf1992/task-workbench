package com.twb.entity;

import lombok.Data;

import javax.persistence.*;

/** 日程事件 */
@Data
@Entity
@Table(name = "schedule_event")
public class ScheduleEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String dkey;  // 2026-9-15
    private String start; // 09:00
    private String title;
    private String desc;
    private String dur;
    private String color; // blue/red/orange/green/purple/gray
}

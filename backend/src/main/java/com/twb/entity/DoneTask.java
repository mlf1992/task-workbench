package com.twb.entity;

import lombok.Data;

import javax.persistence.*;

/** 已完成任务记录 */
@Data
@Entity
@Table(name = "done_task")
public class DoneTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String finishTime;
    private String cost;
    private String owner;
    private String color;
    private String project;
}

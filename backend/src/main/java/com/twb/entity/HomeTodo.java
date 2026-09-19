package com.twb.entity;

import lombok.Data;

import javax.persistence.*;

/** 首页今日待办 */
@Data
@Entity
@Table(name = "home_todo")
public class HomeTodo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String priority;
    private String deadline;
    private String status;   // pending/doing/done
    private Boolean urgent = false;
    private Boolean done = false;
}

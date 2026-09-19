package com.twb.entity;

import lombok.Data;

import javax.persistence.*;

/** 重点事项（首页本周重点 / 任务页本周重点） */
@Data
@Entity
@Table(name = "focus_item")
public class FocusItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String scope;  // home / tasks
    private String name;
    private String status; // doing/todo/pending
}

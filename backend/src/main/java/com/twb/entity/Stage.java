package com.twb.entity;

import lombok.Data;

import javax.persistence.*;

/** 项目阶段节点 */
@Data
@Entity
@Table(name = "project_stage")
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name = "start_day")
    private Integer start;
    @Column(name = "end_day")
    private Integer end;
    private Integer progress;
    private String status;  // done/doing/blocked/todo
    private String owner;
}

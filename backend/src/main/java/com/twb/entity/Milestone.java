package com.twb.entity;

import lombok.Data;

import javax.persistence.*;

/** 项目里程碑 */
@Data
@Entity
@Table(name = "milestone")
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "day_of_month")
    private Integer day;
    private String title;
    private String proj;
    private String level; // risk/plan/done
}

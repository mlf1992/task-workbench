package com.twb.entity;

import lombok.Data;

import javax.persistence.*;

/** 项目风险与阻塞 */
@Data
@Entity
@Table(name = "project_risk")
public class ProjectRisk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String level; // high/mid
    private String title;
    @Column(length = 500)
    private String desc;
    private String proj;
}

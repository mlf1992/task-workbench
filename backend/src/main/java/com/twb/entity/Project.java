package com.twb.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** 项目 */
@Data
@Entity
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String owner;
    private String tone;       // blue/green/orange/purple/cyan/red
    private Integer progress;  // 0-100
    private String status;     // doing/risk/done/todo
    private Integer startDay;  // 9月开始日
    private Integer endDay;    // 9月结束日
    private Integer tasksDone;
    private Integer tasksTotal;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    @OrderColumn(name = "pos")
    private List<Stage> stages = new ArrayList<>();
}

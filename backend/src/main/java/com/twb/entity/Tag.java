package com.twb.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** 标签分类 */
@Data
@Entity
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;  // blue/green/orange/purple/red/cyan
    @Convert(converter = LongListConverter.class)
    @Column(length = 500)
    private List<Long> taskIds = new ArrayList<>(); // 关联任务
}

package com.twb.entity;

import lombok.Data;

import javax.persistence.*;

/** 等待回复事项 */
@Data
@Entity
@Table(name = "waiting_item")
public class WaitingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String source;  // 来源
    private Integer days;   // 等待天数
    private String status;  // waiting / done
}

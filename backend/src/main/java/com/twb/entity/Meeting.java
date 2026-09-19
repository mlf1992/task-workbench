package com.twb.entity;

import lombok.Data;

import javax.persistence.*;

/** 会议纪要 */
@Data
@Entity
@Table(name = "meeting")
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String date;        // 2026-09-15
    private String organizer;   // 组织人
    private String attendees;   // 参与人
    @Column(length = 2000)
    private String summary;     // 纪要正文
    private String tag;         // 类型：例会/评审/沟通
}

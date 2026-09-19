package com.twb.entity;

import lombok.Data;

import javax.persistence.*;

/** 系统设置（key/value） */
@Data
@Entity
@Table(name = "setting")
public class Setting {
    @Id
    @Column(name = "k")
    private String key;
    @Column(name = "v", length = 500)
    private String value;
}

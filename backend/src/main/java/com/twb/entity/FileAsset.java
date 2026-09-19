package com.twb.entity;

import lombok.Data;

import javax.persistence.*;

/** 资料文件（元数据；上传文件另存磁盘） */
@Data
@Entity
@Table(name = "file_asset")
public class FileAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;   // 文档/表格/PDF/演示文稿
    private String time;   // 更新时间展示文案
    private String size;   // 展示大小
    private String owner;
    private String color;
    private String app;    // W / X / P / PDF
    private String path;   // 磁盘相对路径（上传文件才有）
}

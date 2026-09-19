package com.twb.controller;

import com.twb.entity.FileAsset;
import com.twb.repository.FileAssetRepository;
import com.twb.service.ActivityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/** 资料文件：元数据 CRUD + 真实文件上传/下载（落盘 data/uploads） */
@RestController
@RequestMapping("/api")
public class FileController {

    private final FileAssetRepository repo;
    private final ActivityService activity;
    private final Path uploadRoot;

    public FileController(FileAssetRepository repo, ActivityService activity,
                          @Value("${workbench.upload-dir:./data/uploads}") String uploadDir) throws IOException {
        this.repo = repo;
        this.activity = activity;
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath();
        Files.createDirectories(this.uploadRoot);
    }

    @GetMapping("/files")
    public List<FileAsset> list() {
        List<FileAsset> list = repo.findAll();
        list.sort(Comparator.comparing(FileAsset::getTime).reversed());
        return list;
    }

    /** 手工登记一条资料记录 */
    @PostMapping("/files")
    public FileAsset create(@RequestBody FileAsset f) {
        f.setId(null);
        return repo.save(f);
    }

    /** 真实文件上传：文件落盘，元数据入库 */
    @PostMapping("/files/upload")
    public FileAsset upload(@RequestParam("file") MultipartFile file) throws IOException {
        String original = file.getOriginalFilename() == null ? "未命名文件" : file.getOriginalFilename();
        String stored = UUID.randomUUID().toString().replace("-", "") + "_" + original;
        Path target = uploadRoot.resolve(stored);
        file.transferTo(target.toFile());

        FileAsset f = new FileAsset();
        f.setName(original);
        f.setType(guessType(original));
        f.setApp(guessApp(original));
        f.setSize(humanSize(file.getSize()));
        f.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        f.setOwner("我");
        f.setColor(guessColor(original));
        f.setPath(stored);
        FileAsset saved = repo.save(f);
        activity.log("file", "上传资料：" + original, null);
        return saved;
    }

    /** 文件下载 */
    @GetMapping("/files/{id}/raw")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        FileAsset f = repo.findById(id).orElseThrow(() -> new NoSuchElementException("文件不存在"));
        if (f.getPath() == null || f.getPath().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        File file = uploadRoot.resolve(f.getPath()).toFile();
        if (!file.exists()) return ResponseEntity.notFound().build();
        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + f.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/files/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        Optional<FileAsset> opt = repo.findById(id);
        if (opt.isPresent()) {
            FileAsset f = opt.get();
            if (f.getPath() != null && !f.getPath().isEmpty()) {
                try { Files.deleteIfExists(uploadRoot.resolve(f.getPath())); } catch (IOException ignored) { }
            }
            repo.delete(f);
            activity.log("file", "删除资料：" + f.getName(), null);
        }
        return Collections.singletonMap("ok", true);
    }

    private String guessType(String name) {
        String n = name.toLowerCase();
        if (n.endsWith(".xlsx") || n.endsWith(".xls") || n.endsWith(".csv")) return "表格";
        if (n.endsWith(".pdf")) return "PDF";
        if (n.endsWith(".pptx") || n.endsWith(".ppt")) return "演示文稿";
        if (n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg")) return "图片";
        return "文档";
    }

    private String guessApp(String name) {
        String n = name.toLowerCase();
        if (n.endsWith(".xlsx") || n.endsWith(".xls")) return "X";
        if (n.endsWith(".pdf")) return "PDF";
        if (n.endsWith(".pptx") || n.endsWith(".ppt")) return "P";
        if (n.endsWith(".doc") || n.endsWith(".docx")) return "W";
        return "F";
    }

    private String guessColor(String name) {
        switch (guessApp(name)) {
            case "X": return "c-green";
            case "P": return "c-red";
            case "PDF": return "c-orange";
            default: return "c-blue";
        }
    }

    private String humanSize(long bytes) {
        if (bytes >= 1024 * 1024) return String.format(Locale.ROOT, "%.1f MB", bytes / 1024.0 / 1024.0);
        if (bytes >= 1024) return String.format(Locale.ROOT, "%.0f KB", bytes / 1024.0);
        return bytes + " B";
    }
}

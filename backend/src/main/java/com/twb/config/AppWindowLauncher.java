package com.twb.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 独立应用窗口启动器：
 * 优先用系统 Chrome/Edge 的 --app 模式打开「无地址栏、无标签页」的独立窗口，
 * 并使用独立 user-data-dir，从而可以跟踪该进程——窗口关闭即可联动退出整个程序。
 * 找不到 Chromium 内核浏览器时返回 null，由调用方回退到系统默认浏览器。
 */
public final class AppWindowLauncher {

    private AppWindowLauncher() {
    }

    /** 返回应用窗口进程；不支持时返回 null */
    public static Process launch(String url, Path profileDir) {
        String browser = findBrowser();
        if (browser == null) return null;
        try {
            Files.createDirectories(profileDir);
            List<String> cmd = new ArrayList<>(Arrays.asList(
                    browser,
                    "--app=" + url,
                    "--window-size=1440,900",
                    "--no-first-run",
                    "--no-default-browser-check",
                    "--disable-features=Translate",
                    "--user-data-dir=" + profileDir.toAbsolutePath()
            ));
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            // 浏览器 stdout/stderr 落到配置目录日志（Java 8 无 Redirect.DISCARD）
            pb.redirectOutput(profileDir.resolve("launcher.log").toFile());
            return pb.start();
        } catch (Exception e) {
            System.out.println("[桌面模式] 独立窗口启动失败：" + e.getMessage());
            return null;
        }
    }

    /** 默认窗口配置目录：%APPDATA%\TaskWorkbench\app-profile */
    public static Path defaultProfileDir() {
        String appData = System.getenv("APPDATA");
        Path base = (appData != null && !appData.isEmpty())
                ? Paths.get(appData, "TaskWorkbench")
                : Paths.get(System.getProperty("user.home"), ".task-workbench");
        return base.resolve("app-profile");
    }

    private static String findBrowser() {
        String override = System.getenv("TWB_BROWSER");
        if (override != null && !override.isEmpty() && new File(override).isFile()) {
            return override;
        }
        String local = System.getenv("LOCALAPPDATA");
        String progFiles = System.getenv("ProgramFiles");
        String progFilesX86 = System.getenv("ProgramFiles(X86)");
        List<String> candidates = new ArrayList<>();
        if (local != null) {
            candidates.add(local + "\\Google\\Chrome\\Application\\chrome.exe");
        }
        if (progFiles != null) {
            candidates.add(progFiles + "\\Google\\Chrome\\Application\\chrome.exe");
        }
        if (progFilesX86 != null) {
            candidates.add(progFilesX86 + "\\Google\\Chrome\\Application\\chrome.exe");
            candidates.add(progFilesX86 + "\\Microsoft\\Edge\\Application\\msedge.exe");
        }
        if (progFiles != null) {
            candidates.add(progFiles + "\\Microsoft\\Edge\\Application\\msedge.exe");
        }
        for (String c : candidates) {
            if (new File(c).isFile()) return c;
        }
        return null;
    }
}

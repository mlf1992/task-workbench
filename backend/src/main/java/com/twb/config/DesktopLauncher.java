package com.twb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 桌面模式：服务就绪后以「独立应用窗口」打开工作台，并在系统托盘放置图标。
 * 关闭应用窗口即退出整个程序；服务器部署时可用 -Dtwb.desktop.enabled=false 关闭。
 */
@Component
@ConditionalOnProperty(name = "twb.desktop.enabled", havingValue = "true", matchIfMissing = true)
public class DesktopLauncher {

    @Value("${server.port:8080}")
    private String port;

    @Value("${workbench.home:}")
    private String workbenchHome;

    private volatile Process appWindow;

    private String homeUrl() {
        return "http://localhost:" + port + "/";
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        java.awt.EventQueue.invokeLater(() -> {
            installTray();
            openAppWindow(true);
        });
    }

    /** 打开独立应用窗口；track=true 时窗口关闭联动退出整个程序 */
    private synchronized void openAppWindow(boolean track) {
        Path profileDir = (workbenchHome == null || workbenchHome.isEmpty())
                ? AppWindowLauncher.defaultProfileDir()
                : Paths.get(workbenchHome, "app-profile");
        Process p = AppWindowLauncher.launch(homeUrl(), profileDir);
        if (p != null) {
            appWindow = p;
            System.out.println("[桌面模式] 独立应用窗口已打开：" + homeUrl());
            if (track) {
                Thread watcher = new Thread(() -> {
                    try {
                        p.waitFor();
                    } catch (InterruptedException ignored) {
                        return;
                    }
                    System.out.println("[桌面模式] 应用窗口已关闭，程序退出。");
                    System.exit(0);
                }, "app-window-watcher");
                watcher.setDaemon(true);
                watcher.start();
            }
        } else if (track) {
            openDefaultBrowser();
        }
    }

    private void openDefaultBrowser() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(homeUrl()));
            } else {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + homeUrl());
            }
            System.out.println("[桌面模式] 未找到 Chrome/Edge，已用默认浏览器打开：" + homeUrl());
        } catch (Exception e) {
            System.out.println("[桌面模式] 打开页面失败，请手动访问：" + homeUrl() + " (" + e.getMessage() + ")");
        }
    }

    private void installTray() {
        try {
            if (!SystemTray.isSupported()) return;
            PopupMenu menu = new PopupMenu();

            MenuItem openItem = new MenuItem("显示工作台窗口");
            openItem.addActionListener(e -> openAppWindow(false));
            TrayIcon[] holder = new TrayIcon[1];
            MenuItem exitItem = new MenuItem("退出程序");
            exitItem.addActionListener(e -> {
                if (holder[0] != null) SystemTray.getSystemTray().remove(holder[0]);
                System.exit(0);
            });
            menu.add(openItem);
            menu.addSeparator();
            menu.add(exitItem);

            TrayIcon trayIcon = new TrayIcon(buildIcon(), "任务安排工作台（运行中）", menu);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> openAppWindow(false));
            SystemTray.getSystemTray().add(trayIcon);
            holder[0] = trayIcon;
        } catch (AWTException | HeadlessException e) {
            System.out.println("[桌面模式] 系统托盘不可用。");
        }
    }

    /** 程序化生成 16x16 托盘图标（蓝色圆角底 + 白色「台」字），不依赖外部图片文件 */
    private Image buildIcon() {
        int size = 16;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(0x3D6BFF));
        g.fillRoundRect(0, 0, size, size, 4, 4);
        g.setColor(Color.WHITE);
        g.setFont(new Font("微软雅黑", Font.BOLD, 11));
        java.awt.FontMetrics fm = g.getFontMetrics();
        String s = "台";
        int tx = (size - fm.stringWidth(s)) / 2;
        int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(s, tx, ty);
        g.dispose();
        return img;
    }
}

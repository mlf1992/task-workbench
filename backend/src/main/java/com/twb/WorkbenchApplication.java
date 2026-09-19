package com.twb;

import com.twb.config.AppWindowLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.Desktop;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

@SpringBootApplication
public class WorkbenchApplication {

    public static void main(String[] args) {
        // 单实例保护：已有实例在监听端口时，只打开独立窗口并退出，不再启动第二个 JVM
        int port = resolvePort(args);
        if (isPortOpen("127.0.0.1", port)) {
            String url = "http://localhost:" + port + "/";
            if (AppWindowLauncher.launch(url, AppWindowLauncher.defaultProfileDir()) == null) {
                openBrowserQuietly(url);
            }
            System.exit(0);
        }

        // 桌面模式需要 AWT（系统托盘、打开浏览器），关闭 headless
        SpringApplication app = new SpringApplication(WorkbenchApplication.class);
        app.setHeadless(false);
        app.run(args);
    }

    private static int resolvePort(String[] args) {
        int port = 8080;
        String env = System.getenv("SERVER_PORT");
        if (env != null && env.matches("\\d+")) port = Integer.parseInt(env);
        String prop = System.getProperty("server.port");
        if (prop != null && prop.matches("\\d+")) port = Integer.parseInt(prop);
        for (String a : args) {
            if (a != null && a.startsWith("--server.port=")) {
                String v = a.substring("--server.port=".length());
                if (v.matches("\\d+")) port = Integer.parseInt(v);
            }
        }
        return port;
    }

    private static boolean isPortOpen(String host, int port) {
        try (Socket s = new Socket()) {
            s.connect(new InetSocketAddress(host, port), 400);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void openBrowserQuietly(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            } else {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            }
        } catch (Exception ignored) {
        }
    }
}

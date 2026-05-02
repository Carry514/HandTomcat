package org.gao.page05;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page05
 * @Author: 高
 * @CreateTime: 2025-06-15 21:35
 * @Description: 本模块最终版，整合 while(true) 循环、请求解析、完整 HTTP 响应及错误处理
 * @Version: 1.0
 */

public class MyHttpServer {
    // 基础图片目录路径
    private static final String BASE_DIR = "webapps/static/images";
    // MIME类型映射
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("jpg", "image/jpeg");
        MIME_TYPES.put("jpeg", "image/jpeg");
        MIME_TYPES.put("png", "image/png");
        MIME_TYPES.put("gif", "image/gif");
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("服务端启动了，端口为：9090");

        // 持续监听客户端连接
        while (true) {
            try (Socket socket = serverSocket.accept();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 OutputStream os = socket.getOutputStream()) {

                // 1. 读取并解析HTTP请求
                String requestLine = reader.readLine();
                if (requestLine == null || requestLine.isEmpty()) continue;

                // 2. 提取请求路径 (如 GET /aaa.jpg HTTP/1.1)
                String[] requestParts = requestLine.split(" ");
                if (requestParts.length < 2) {
                    sendError(os, 400, "Bad Request");
                    continue;
                }
                String requestPath = requestParts[1];

                // 3. 构建本地文件路径
                Path filePath = Paths.get(BASE_DIR, requestPath.substring(1)); // 移除路径前的"/"

                // 4. 安全校验：防止路径遍历攻击
                if (!filePath.normalize().startsWith(Paths.get(BASE_DIR).normalize())) {
                    sendError(os, 403, "Forbidden");
                    continue;
                }

                // 5. 检查文件是否存在
                if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                    sendError(os, 404, "Not Found");
                    continue;
                }

                // 6. 动态设置Content-Type
                String fileExt = getFileExtension(filePath);
                String contentType = MIME_TYPES.getOrDefault(fileExt.toLowerCase(), "application/octet-stream");

                // 7. 读取文件并发送响应
                byte[] fileBytes = Files.readAllBytes(filePath);
                sendResponse(os, 200, "OK", contentType, fileBytes);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    private static void sendResponse(OutputStream os, int statusCode, String statusText,
                                     String contentType, byte[] content) throws IOException {
        // 构建HTTP响应
        String responseLine = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n";
        String headers = "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + content.length + "\r\n" +
                "Connection: close\r\n\r\n";

        // 发送响应
        os.write(responseLine.getBytes());
        os.write(headers.getBytes());
        os.write(content);
        os.flush();
    }

    private static void sendError(OutputStream os, int statusCode, String statusText) throws IOException {
        String errorHtml = "<html><body><h1>" + statusCode + " " + statusText + "</h1></body></html>";
        sendResponse(os, statusCode, statusText, "text/html", errorHtml.getBytes());
    }
}

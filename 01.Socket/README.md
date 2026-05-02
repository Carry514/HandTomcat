# 01.Socket — 基础 Socket 通信

## 学习目标

- 理解 `ServerSocket` / `Socket` 的通信模型
- 理解浏览器发送的 HTTP 请求报文格式
- 手写 HTTP 响应报文，让浏览器正确渲染内容

## 前置知识

无。这是本项目的第一个模块，从零开始。

## 核心知识点

### 1. ServerSocket 通信模型

```
服务端                              客户端（浏览器）
  │                                     │
  │  new ServerSocket(9090)             │
  │         │                           │
  │         ▼                           │
  │  serverSocket.accept() ◀━━━━━━━━ new Socket("ip", 9090)
  │         │                           │
  │         ▼                           │
  │  socket.getInputStream() ◀━━━━━━ socket.getOutputStream()
  │         │                           │
  │         ▼                           │
  │  socket.getOutputStream() ━━━━━━▶ socket.getInputStream()
```

- `accept()` 是**阻塞**方法，没有客户端连接时会一直等待
- 每个客户端连接返回一个独立的 `Socket` 对象
- 通过 `Socket` 获取的 `InputStream` / `OutputStream` 进行双向通信

### 2. HTTP 请求报文结构

浏览器发送的原始请求长这样：

```
GET /111.jpg HTTP/1.1
Host: localhost:9090
Connection: keep-alive
User-Agent: Mozilla/5.0 ...
Accept: image/avif,image/webp,...

```

- **请求行**：`方法 路径 协议版本`
- **请求头**：`Key: Value` 键值对
- **请求体**：GET 请求通常为空

### 3. HTTP 响应报文结构

```text
HTTP/1.1 200 OK\r\n                  ← 状态行
Content-Type: text/html;charset=UTF-8\r\n  ← 响应头
Content-Length: 6\r\n                ← 响应头
\r\n                                  ← 空行（分隔符）
<html>...</html>                      ← 响应体
```

**关键点**：响应头与响应体之间必须有一个空行（`\r\n\r\n`），浏览器靠这个空行区分头部和正文。

### 4. MIME 类型

浏览器根据 `Content-Type` 决定如何渲染内容：

| Content-Type | 效果 |
|---|---|
| `text/html` | 渲染为网页 |
| `image/png` | 显示图片 |
| `image/jpeg` | 显示图片 |
| `application/octet-stream` | 触发下载 |

## page 演进说明

### page01 — 最简通信

**关注文件**：[MyServer.java](src/org/gao/page01/MyServer.java)、[MyClient.java](src/org/gao/page01/MyClient.java)

- 服务端 `accept()` 一次，读取客户端发来的字节，打印到控制台
- 客户端发送一个字符串
- **要点**：理解 `ServerSocket → accept → Socket → InputStream` 这条链路

### page02 — 循环收发

**关注文件**：[MyServer.java](src/org/gao/page02/MyServer.java)、[MyClient.java](src/org/gao/page02/MyClient.java)

- 服务端加 `while(true)` 持续读取，客户端加 `Scanner` 持续发送
- **问题**：只接收不回复，浏览器会一直转圈等待

### page03 — 首次 HTTP 响应

**关注文件**：[MyServer.java](src/org/gao/page03/MyServer.java)

- 用浏览器访问 `http://localhost:9090`
- 手写了状态行 `HTTP/1.1 200 OK`、响应头 `Content-Type` 和 `Content-Length`，返回文字"你好"
- **要点**：理解 HTTP 响应报文的格式，特别是**空行分隔**的作用

### page04 — 响应图片

**关注文件**：[MyServer.java](src/org/gao/page04/MyServer.java)

- 读取本地图片文件 `webapps/static/images/333.jpg`
- 设置 `Content-Type: image/png`，将图片字节数组写入响应体
- **说明**：此处仅演示图片响应的基本流程，Content-Type 硬编码、单次处理等限制将在后续 page 中逐步解决

### page05 — 动态 MIME + 完整 HTTP 服务

**关注文件**：[MyServer.java](src/org/gao/page05/MyServer.java)、[MyHttpServer.java](src/org/gao/page05/MyHttpServer.java)

- `MyServer.java`：使用 `MimetypesFileTypeMap` 自动探测文件 MIME 类型
- `MyHttpServer.java`：本模块的**最终版**，实现了：
  - `while(true)` 循环持续处理多个请求
  - 解析请求行，提取请求路径
  - 文件后缀 → MIME 映射（jpg/png/gif）
  - 404/400/403 错误处理
  - 路径遍历攻击防护（`normalize()` 校验）

## 关键代码导读

| 文件 | 行号 | 要点 |
|------|------|------|
| [page01/MyServer.java:20-23](src/org/gao/page01/MyServer.java) | 20-23 | `ServerSocket(9090)` 创建 → `accept()` 阻塞等待 |
| [page03/MyServer.java:37-44](src/org/gao/page03/MyServer.java) | 37-44 | 手写 HTTP 响应报文，**注意空行 `\r\n\r\n`** |
| [page04/MyServer.java:32-35](src/org/gao/page04/MyServer.java) | 32-35 | `FileInputStream` 读取文件到字节数组 |
| [page05/MyHttpServer.java:48-54](src/org/gao/page05/MyHttpServer.java) | 48-54 | 从请求行 `GET /aaa.jpg HTTP/1.1` 中 split 提取路径 |
| [page05/MyHttpServer.java:57-63](src/org/gao/page05/MyHttpServer.java) | 57-63 | 路径遍历防护 — 防止 `../../etc/passwd` 攻击 |
| [page05/MyHttpServer.java:91-103](src/org/gao/page05/MyHttpServer.java) | 91-103 | 封装 `sendResponse()` 方法，构建完整 HTTP 响应 |

## 与真实 Tomcat 的对应

| 本模块 | Tomcat 概念 | 说明 |
|--------|------------|------|
| `ServerSocket(9090)` | Connector 的 Endpoint | 监听端口的底层实现 |
| `socket.accept()` | Acceptor 线程 | 等待并接受新连接 |
| `requestLine.split(" ")` | Request 解析 | Tomcat 中用 `Http11Processor` 完成 |
| `sendResponse()` | Response 输出 | Tomcat 中用 `Http11OutputBuffer` 完成 |
| `while(true)` 循环 | 事件循环 | Tomcat 用 NIO Selector 实现，本质相同 |

## 运行方式

### page01 — 最简通信

1. 先启动 `MyServer.java`
2. 再启动 `MyClient.java`，消息在代码中写死，直接发送

### page02 — 循环收发

1. 先启动 `MyServer.java`
2. 再启动 `MyClient.java`，在控制台输入消息发送

> page01/02 只接收不回复，用浏览器访问会一直等待超时，请用 MyClient 测试。

### page03 — 浏览器访问

1. 启动 `MyServer.java`
2. 打开浏览器访问 `http://localhost:9090`
3. 页面显示"你好"

### page04 — 响应图片

1. 启动 `MyServer.java`
2. 打开浏览器访问 `http://localhost:9090`
3. 页面显示图片 `webapps/static/images/333.jpg`

### page05 — 完整 HTTP 服务

1. 启动 `MyServer.java` 或 `MyHttpServer.java`
2. 将图片放入 `webapps/static/images/` 目录
3. 打开浏览器访问 `http://localhost:9090/图片文件名`
   - 例如：`http://localhost:9090/333.jpg`
4. 文件不存在时返回 404 页面

> 所有图片、HTML 等静态资源统一放在项目根目录的 `webapps/` 下。

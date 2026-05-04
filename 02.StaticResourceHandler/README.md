# 02.StaticResourceHandler — 静态资源处理

## 学习目标

- 掌握 HTTP 请求报文的**手动解析**（split 方式拆分请求行/头/体）
- 理解如何将解析逻辑封装为独立的 `MyHttpRequest` / `MyHttpResponse` 类
- 掌握静态资源处理器的设计：文件读取 + MIME 类型探测

## 前置知识

需要先理解 [01.Socket](../01.Socket/README.md) 中的 ServerSocket 通信模型和 HTTP 响应报文结构。

## 核心知识点

### 1. 请求报文解析流程

```
原始请求报文（字符串）
        │
        ▼
  split("\r\n")       →  拆分为"请求行"和后续各行
        │
        ├── 第0行  →  split(" ")  →  [方法, URL, 协议版本]
        │
        ├── 第1~N行 →  请求头  →  split(": ")  →  [Key, Value]
        │
        └── 最后一行 →  请求体（POST 时有效）
```

**要点**：
- 请求行、请求头、空行、请求体之间用 `\r\n` 分隔
- GET 参数拼在 URL 中，通过 `?` 分割，参数间用 `&` 连接
- POST 参数放在请求体中，格式与 GET 参数相同

### 2. 类的职责拆分

本模块的核心演进是将"一坨代码"拆分为三个职责清晰的类：

```
┌─────────────────┐
│  MyHttpRequest  │  → 解析原始报文 → 提取 method / URL / params / headers / body
├─────────────────┤
│ StaticResource  │  → 根据文件路径 → 读取字节 + 探测 MIME 类型
│    Handler      │
├─────────────────┤
│ MyHttpResponse  │  → 统一输出接口 → write(media, bytes)
└─────────────────┘
```

这对应了真实 Tomcat 中 `Request` → `ResourceHandler` → `Response` 的架构雏形。

**为什么要拆分？** 一句话：**封装变化，职责分离。**

- `MyServer` 的理想角色是"调度员"——接请求、找资源、写响应。如果还要负责 split 字符串、判断问号、切 `&` 切 `=`，就成了"调度员 + 协议解析器"，职责混乱
- **可复用**：后续 03~05 模块全部复用 `MyHttpRequest`，不用重写解析逻辑
- **可独立演进**：page02 → page03 增强请求头解析时，只改 `MyHttpRequest`，`MyServer` 不用动
- **贴近真实设计**：Tomcat 的 `HttpServletRequest`（解析请求）与 `HttpServletResponse`（构造响应）正是这个思路，各自封装自己的协议细节

### 3. MIME 类型探测

通过文件后缀判断媒体类型：

```java
String suffix = filePath.split("\\.")[last];
if (suffix.equals("html")) → text/html
if (suffix.equals("jpg"))  → image/jpg
if (suffix.equals("png"))  → image/png
```

后续模块会改用 `MimetypesFileTypeMap` 自动探测，这里先手动实现以理解原理。

## page 演进说明

### page01 — 手动解析请求行

**关注文件**：[MyServer.java](src/org/gao/page01/MyServer.java)

- 用 `split("\r\n")` 拆分请求报文，取第一行作为请求行
- 从请求行中 split 提取 URL，拼接本地路径 `webapps/static/images` + URL
- 用 `FileNotFoundException` 异常来做 404 兜底
- 后缀判断 MIME（html / jpg / png）

> 此版本将所有逻辑写在一个类中，虽然能跑，但解析、文件读取、响应输出耦合在一起。

### page02 — 抽取 MyHttpRequest

**关注文件**：[MyServer.java](src/org/gao/page02/MyServer.java)、[MyHttpRequest.java](src/org/gao/page02/MyHttpRequest.java)

- 新增 `MyHttpRequest` 类，构造时传入原始报文字符串，自动解析
- 解析能力：请求方法、URL、协议版本、GET 参数（`?` 分割 + `&` 分割）、POST 请求体
- `MyServer` 不再手动 split，改为 `httpRequest.getRequestURL()` 获取路径
- `praseRequestHead()` 方法已定义但未实现，留到 page03

### page03 — 完整类拆分（本模块最终版）

**关注文件**：[MyServer.java](src/org/gao/page03/MyServer.java)、[MyHttpRequest.java](src/org/gao/page03/MyHttpRequest.java)、[MyHttpResponse.java](src/org/gao/page03/MyHttpResponse.java)、[StaticResourceHandler.java](src/org/gao/page03/StaticResourceHandler.java)

四个类的分工：

| 类 | 职责 | 关键方法 |
|----|------|---------|
| `MyHttpRequest` | 解析请求报文 | `praseRequestLine()` / `praseRequestHead()` / `praseRequestBody()` |
| `StaticResourceHandler` | 读取文件 + MIME | `getFileByte()` / `getFileMedia()` |
| `MyHttpResponse` | 输出 HTTP 响应 | `write(media, bytes)` |
| `MyServer` | 协调调度 | `while(true)` 循环 + 路由判断 |

page03 相比 page02 的新增：
- `MyHttpRequest` 新增请求头解析（遍历数组、`split(": ")` 拆分键值对）
- 新增数组越界防护（`split` 后检查 `length > 1`）
- `MyHttpResponse` 封装 HTTP 响应输出，统一 `write(media, bytes)` 接口
- `StaticResourceHandler` 封装文件读取和 MIME 探测
- `MyServer` 加入 `while(true)` 循环、根路径 `/` → 首页、文件不存在 → 404

## 关键代码导读

| 文件 | 行号 | 要点 |
|------|------|------|
| [page01/MyServer.java:34-41](src/org/gao/page01/MyServer.java) | 34-41 | `split("\r\n")` 拆分请求 → 取请求行 → `split(" ")` 取 URL |
| [page01/MyServer.java:72-78](src/org/gao/page01/MyServer.java) | 72-78 | 用 `FileNotFoundException` 异常实现 404 兜底 |
| [page01/MyServer.java:86-94](src/org/gao/page01/MyServer.java) | 86-94 | 后缀判断 MIME 类型 |
| [page02/MyHttpRequest.java:64-83](src/org/gao/page02/MyHttpRequest.java) | 64-83 | 构造函数 → `praseRequsetMSG()` → 按 `\r\n` 切割 → 分发到各解析方法 |
| [page02/MyHttpRequest.java:109-128](src/org/gao/page02/MyHttpRequest.java) | 109-128 | GET 参数解析：`?` 分割 URL → `&` 分割参数 → `=` 分割键值对 |
| [page03/MyHttpRequest.java:138-148](src/org/gao/page03/MyHttpRequest.java) | 138-148 | 请求头解析：遍历 `splitRequestMSGArray`，按 `: ` 拆分键值对 |
| [page03/MyHttpResponse.java:26-43](src/org/gao/page03/MyHttpResponse.java) | 26-43 | 封装 `write()` 方法，统一输出状态行 + 响应头 + 空行 + 响应体 |
| [page03/StaticResourceHandler.java:42-49](src/org/gao/page03/StaticResourceHandler.java) | 42-49 | `FileInputStream` 读取文件到字节数组 |
| [page03/StaticResourceHandler.java:56-67](src/org/gao/page03/StaticResourceHandler.java) | 56-67 | 按文件后缀匹配 MIME 类型 |

## 与真实 Tomcat 的对应

| 本模块 | Tomcat 概念 | 说明 |
|--------|------------|------|
| `MyHttpRequest.parseRequestLine()` | `Http11Processor.parseRequestLine()` | 解析 HTTP 请求行 |
| `MyHttpRequest.parseRequestHead()` | `Http11Processor.parseHeaders()` | 解析 HTTP 请求头 |
| `MyHttpResponse.write()` | `Http11OutputBuffer.sendResponse()` | 写入 HTTP 响应 |
| `StaticResourceHandler` | `DefaultServlet` / `WebResourceRoot` | 处理静态资源请求 |
| `MyServer` 中的路由判断 | `Mapper.map()` | URL → 资源映射 |

## 运行方式

### page01 / page02

1. 启动 `MyServer.java`
2. 浏览器访问 `http://localhost:9090/333.jpg`
3. 图片放在 `webapps/static/images/` 目录下

### page03

1. 启动 `MyServer.java`
2. 浏览器访问：
   - `http://localhost:9090/` → 首页（`webapps/pages/index.html`）
   - `http://localhost:9090/333.jpg` → 图片（`webapps/static/images/333.jpg`）
   - 访问不存在的路径 → 404 页面

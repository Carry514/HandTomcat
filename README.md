# HandTomcat — 手写迷你 Tomcat

从零开始，用纯 Java 手写一个迷你 Tomcat 容器，理解 HTTP 服务器底层原理。

## 项目简介

Tomcat 是 Java Web 开发中最常用的 Servlet 容器，但它的源码庞大复杂。本项目通过 **渐进式演进** 的方式，用最少的代码实现 Tomcat 的核心机制，帮助理解：

- HTTP 协议解析（请求/响应报文）
- Servlet 容器工作原理
- 多线程请求处理
- 注解扫描与路由映射
- 数据库连接池集成

## 学习路线

```
01.Socket              → 基础 Socket 通信
02.StaticResource      → HTTP 协议解析 + 静态资源返回
03.Thread              → 多线程 + MVC 三层架构
04.DynamicResource     → Servlet 机制 + 注解路由
05.DBPool              → 连接池 + 线程池，最终完整版
```

每个模块包含多个 `page` 子目录，展示从简陋到完善的演进过程，建议按序号逐个阅读。

## 项目结构

```
HandTomcat/
├── 01.Socket/                  # 模块1：Socket 基础
│   └── src/org/gao/page01~05/  # 5 个演进步骤
├── 02.StaticResourceHandler/   # 模块2：静态资源处理
│   └── src/org/gao/page01~03/
├── 03.Thread/                  # 模块3：多线程支持
│   └── src/org/gao/page01~03/
├── 04.DynamicResource/         # 模块4：动态资源（Servlet）
│   └── src/org/gao/page01~04/
├── 05.DBPool/                  # 模块5：数据库连接池
│   └── src/org/gao/page01~02/
├── config/                     # 配置文件
│   ├── servlet.properties      # Servlet 路由映射
│   └── druid.properties        # 数据库连接池配置
└── webapps/                    # Web 静态资源
    ├── pages/                  # HTML 页面
    └── static/images/          # 图片资源
```

## 环境配置

### 基础环境

| 工具 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 1.8+ | 编译和运行 |
| IntelliJ IDEA | 任意版本 | 打开项目，识别多模块结构 |

### 数据库（模块 03~05 需要）

1. 安装 MySQL，创建数据库：
   ```sql
   CREATE DATABASE handtomcat;
   ```
2. 执行 [config/init.sql](config/init.sql) 建表并插入测试数据
3. 修改 [config/druid.properties](config/druid.properties) 中的数据库连接信息：
   ```properties
   url=jdbc:mysql://localhost:3306/handtomcat
   username=你的用户名
   password=你的密码
   ```

### 导入项目

1. 用 IntelliJ IDEA 选择 **Open**，打开项目根目录
2. IDEA 会自动识别各模块的 `.iml` 配置
3. 各模块 `lib/` 目录下的 jar 包需手动添加到模块依赖（或忽略报错，仅阅读源码）

## 快速开始

1. 用 IntelliJ IDEA 打开项目根目录
2. 从 `01.Socket` 开始，按模块顺序阅读
3. 每个模块的 `page01` 是最简版本，`page0N` 是最终版本
4. 运行任意 `APP.java` 或 `MyServer.java` 启动对应版本的服务器

> 模块 03~05 需要 MySQL 数据库，请先执行建表 SQL 并修改 `config/druid.properties`。

## 核心架构（最终版）

```
浏览器请求 :9090
      │
      ▼
┌─────────────┐
│   APP.java  │  ServerSocket(9090) + ThreadPoolExecutor
└──────┬──────┘
       │ accept()
       ▼
┌─────────────┐
│  MyTask.java │  Runnable 任务，解析请求 + 路由分发
└──────┬──────┘
       │
       ├── 命中 @ServletMapping ──▶ LoginServlet / EnrollServlet 等
       │
       └── 未命中 ────────────────▶ DefaultServlet → StaticResourceHandler
                                          │
                                          ▼
                               webapps/ 目录下的静态文件
```

## 依赖说明

项目使用原生 JDBC + 以下第三方库：

| 库 | 用途 | 使用模块 |
|---|---|---|
| fastjson2 | JSON 序列化 | 03~05 |
| mysql-connector-j | MySQL 驱动 | 03~05 |
| druid | 数据库连接池 | 04~05 |
| reflections | 类路径注解扫描 | 04~05 |
| guava | 基础工具库 | 05 |
| javassist | 字节码操作 | 05 |

## 参考

- [Apache Tomcat 架构](https://tomcat.apache.org/tomcat-9.0-doc/architecture/)
- 《How Tomcat Works》— Budi Kurniawan

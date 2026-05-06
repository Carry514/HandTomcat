# 03.Thread — 多线程支持

## 学习目标

- 理解 Java 多线程的两种实现方式：继承 `Thread` 与实现 `Runnable`
- 掌握 HTTP 服务器的多线程模型：一个连接一个线程
- 理解完整 MVC 三层架构（servlet / service / dao）的职责划分
- 掌握 `BaseServlet` 的 GET/POST 分发模式

## 前置知识

需要先理解 [02.StaticResourceHandler](../02.StaticResourceHandler/README.md) 中的 MyHttpRequest / MyHttpResponse / StaticResourceHandler 类拆分。

## 核心知识点

### 1. 多线程模型

```
MyServer
  │
  │  while(true)
  │
  ├── accept() ──▶ Socket1 ──▶ new Thread(MyTask1).start()
  ├── accept() ──▶ Socket2 ──▶ new Thread(MyTask2).start()
  ├── accept() ──▶ Socket3 ──▶ new Thread(MyTask3).start()
  ...
```

- 主线程负责 `accept()`，不处理业务逻辑
- 每个客户端连接分配一个独立线程，避免请求互相阻塞
- `MyTask` 实现 `Runnable`（而不是继承 `Thread`），更灵活

### 2. 静态资源 vs 动态资源路由

```java
// page03 MyTask.run() 中的路由逻辑
if (requestModel 含 "." || requestModel.equals("/")) {
    // 静态资源 → StaticResourceHandler
} else {
    // 动态资源 → switch(requestModel) → 对应 Servlet
}
```

**判断依据**：URL 是否包含文件后缀。`/login`、`/enroll` 不含 `.`，交给 Servlet 处理；`/333.jpg`、`/index.html` 含 `.`，走静态资源。

### 3. BaseServlet 分发模式

```
handlerServlet(request, response)
  │
  ├── GET  ──▶ doGet(request, response)
  └── POST ──▶ doPost(request, response)
```

- `BaseServlet` 是抽象类，定义 `doGet`/`doPost` 抽象方法
- 子类（`LoginServlet`、`EnrollServlet`）重写这两个方法
- 调用方只需 `servlet.handlerServlet(req, res)`，不用关心 HTTP 方法

> 这就是迷你版 Tomcat 的 `HttpServlet.service()` → `doGet()`/`doPost()` 模式。

### 4. MVC 三层架构

```
┌──────────────────────────────────────┐
│  servlet 层（表现层）                 │
│  LoginServlet / EnrollServlet        │
│  职责：接收参数 → 调用 service → 输出  │
├──────────────────────────────────────┤
│  service 层（业务逻辑层）              │
│  UserServiceImpl                     │
│  职责：业务判断 → 调用 DAO → 包装结果  │
├──────────────────────────────────────┤
│  dao 层（数据访问层）                  │
│  UserDaoImpl                         │
│  职责：JDBC 操作数据库                 │
├──────────────────────────────────────┤
│  entity / dto（数据模型）             │
│  StudentDO / ResponseDTO             │
└──────────────────────────────────────┘
```

**每层的返回格式**：
- DAO 返回原始类型或实体（`StudentDO`、`Integer`）
- Service 调用 DAO 后包装为 `ResponseDTO`（统一格式 `{code, msg, data}`）
- Servlet 获取参数 + 调用 Service + 用 fastjson2 序列化 ResponseDTO 为 JSON 输出

## page 演进说明

### page01 — 线程基础

**关注文件**：[MyThread.java](src/org/gao/page01/MyThread.java)、[MyRunnable.java](src/org/gao/page01/MyRunnable.java)

- `MyThread`：继承 `Thread`，重写 `run()`，调用 `start()` 启动
- `MyRunnable`：实现 `Runnable`，通过 `new Thread(runnable).start()` 启动
- 两者各创建两个线程，并发打印数字

> 纯粹演示 Java 多线程的两种写法，不涉及 HTTP。理解后看 page02 就能明白为什么 `MyTask implements Runnable`。

### page02 — 多线程 + 静态资源

**关注文件**：[MyServer.java](src/org/gao/page02/MyServer.java)、[MyTask.java](src/org/gao/page02/MyTask.java)

- `MyServer`：`while(true)` accept 后 `new Thread(new MyTask(socket)).start()`
- `MyTask`：实现 `Runnable`，`run()` 内复用 02 模块的解析/响应逻辑
- 只处理静态资源（读文件、返回 404）

> 本质就是把 02.StaticResourceHandler 的代码套了一层线程。一个连接一个线程，请求不会互相阻塞。

### page03 — 完整 MVC（本模块最终版）

**关注文件**：21 个文件，分 6 个层级

| 层级 | 文件 |
|------|------|
| 入口 + 路由 | `MyServer.java`、`MyTask.java` |
| 协议处理 | `MyHttpRequest.java`、`MyHttpResponse.java` |
| 静态资源 | `StaticResourceHandler.java` |
| 表现层 | `BaseServlet.java`、`LoginServlet.java`、`EnrollServlet.java` |
| 业务层 | `IUserService.java`、`UserServiceImpl.java` |
| 数据层 | `IUserDAO.java`、`UserDaoImpl.java` |
| 数据模型 | `StudentDO.java`、`ResponseDTO.java` |

完整请求链路（以 `/login` 为例）：

```
浏览器 POST /login?account=admin&password=123456
  │
  ▼
MyServer.accept() → new Thread(MyTask).start()
  │
  ▼
MyTask.run()
  ├── new MyHttpRequest(requestMsg)         // 解析报文
  ├── 判断 requestModel 不含 "."             // 动态资源
  ├── switch: "/login"
  └── new LoginServlet().handlerServlet()
        │
        ├── BaseServlet 判断 POST → doPost()
        │
        ▼
      LoginServlet.doPost()
        ├── httpRequest.getRequestParamToKey("account")  // "admin"
        ├── httpRequest.getRequestParamToKey("password")  // "123456"
        ├── new UserServiceImpl().login(acc, pwd)
        │     ├── new UserDaoImpl().login(acc, pwd)
        │     │     └── JDBC select → StudentDO / null
        │     └── 包装为 ResponseDTO
        └── httpResponse.write(JSON.toJSONBytes(dto))
```

**page03 相比 page02 的核心变化**：

| 变化 | 说明 |
|------|------|
| MyTask 路由分化 | 判断 URL 含"."→静态，不含→动态 switch |
| BaseServlet 模式 | 抽象 GET/POST 分发，子类只需重写 doGet/doPost |
| MVC 三层 | servlet → service → dao，每层职责明确 |
| ResponseDTO | 统一 `{code, msg, data}` 响应格式 |
| JDBC 数据库 | 原生 DriverManager + PreparedStatement 连接 MySQL |
| fastjson2 | JSON 序列化 ResponseDTO 输出 |

**MyHttpRequest/MyHttpResponse 的增强**：
- `MyHttpRequest` 新增 `getRequestParamToKey(key)`，Servlet 取参更方便
- `MyHttpResponse` 新增 `write(byte[] bytes)` 重载（默认 `text/html`）

## 关键代码导读

| 文件 | 行号 | 要点 |
|------|------|------|
| [page01/MyThread.java:21-22](src/org/gao/page01/MyThread.java) | 21-22 | 继承 Thread → new → start() |
| [page01/MyRunnable.java:31-32](src/org/gao/page01/MyRunnable.java) | 31-32 | 实现 Runnable → new Thread(runnable).start() |
| [page02/MyServer.java:27](src/org/gao/page02/MyServer.java) | 27 | `while(true)` accept → `new Thread(MyTask).start()` |
| [page02/MyTask.java:26-52](src/org/gao/page02/MyTask.java) | 26-52 | Runnable.run() 内的请求处理全流程 |
| [page03/MyTask.java:36-39](src/org/gao/page03/MyTask.java) | 36-39 | 静态/动态资源判断：`requestModel.contains(".")` |
| [page03/MyTask.java:46-50](src/org/gao/page03/MyTask.java) | 46-50 | switch 路由分发到 LoginServlet / EnrollServlet |
| [page03/BaseServlet.java:19-25](src/org/gao/page03/servlet/BaseServlet.java) | 19-25 | `handlerServlet()` 按 GET/POST 分发到 doGet/doPost |
| [page03/LoginServlet.java:28-32](src/org/gao/page03/servlet/LoginServlet.java) | 28-32 | 取参数 → 调用 Service → JSON 序列化输出 |
| [page03/ResponseDTO.java:22-23](src/org/gao/page03/dto/ResponseDTO.java) | 22-23 | 工厂方法 `success()`/`error()` 创建统一响应 |
| [page03/UserDaoImpl.java:29-41](src/org/gao/page03/dao/impl/UserDaoImpl.java) | 29-41 | 静态块加载 druid.properties 配置驱动和连接信息 |
| [page03/UserDaoImpl.java:46-59](src/org/gao/page03/dao/impl/UserDaoImpl.java) | 46-59 | try-with-resources JDBC：Connection → PreparedStatement → ResultSet |

## 与真实 Tomcat 的对应

| 本模块 | Tomcat 概念 | 说明 |
|--------|------------|------|
| `while(true) + new Thread()` | Connector + Executor | Tomcat 用线程池，本质相同 |
| `MyTask` (Runnable) | `SocketProcessor` | 封装单个请求的处理任务 |
| `BaseServlet.handlerServlet()` | `HttpServlet.service()` | GET/POST 分发的核心模式 |
| `LoginServlet` / `EnrollServlet` | 自定义 Servlet | 用户编写的业务 Servlet |
| `switch(requestModel)` 路由 | `Mapper.map()` | URL → Servlet 映射 |
| `ResponseDTO` | `HttpServletResponse` | 统一响应格式 |
| `UserDaoImpl` (JDBC) | JPA / MyBatis | Tomcat 不直接管 DAO，但真实项目必含此层 |

## 运行方式

### page01

1. 直接运行 `MyThread.java` 或 `MyRunnable.java`
2. 控制台交替打印两个线程的输出

### page02

1. 启动 `MyServer.java`
2. 浏览器访问 `http://localhost:9090/333.jpg`
3. 可同时开多个浏览器标签页，观察并发处理

### page03

1. 确保 MySQL 已启动，执行 [config/init.sql](../config/init.sql)
2. 启动 `MyServer.java`
3. 使用 Postman 或浏览器测试：
   - 静态资源：`http://localhost:9090/static/images/333.jpg`
   - 登录：`POST http://localhost:9090/login?account=admin&password=123456`
   - 注册：`POST http://localhost:9090/enroll?account=new&password=123&name=新人`
4. 返回 JSON 格式：`{"code":1, "msg":"success", "data":{...}}` `{"code":1,"data":{"name":"张三"},"msg":"success"}`

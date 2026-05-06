# 03.Thread — Multi-Threading Support

## Learning Objectives

- Understand the two ways to create threads in Java: extending `Thread` vs implementing `Runnable`
- Master the multi-threaded HTTP server model: one connection per thread
- Understand the responsibility separation of a full MVC three-tier architecture (servlet / service / dao)
- Master the `BaseServlet` GET/POST dispatch pattern

## Prerequisites

Understand the MyHttpRequest / MyHttpResponse / StaticResourceHandler class separation from [02.StaticResourceHandler](../02.StaticResourceHandler/README.md).

## Core Concepts

### 1. Multi-Threaded Model

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

- The main thread handles `accept()` and does not process business logic
- Each client connection gets its own independent thread, preventing requests from blocking each other
- `MyTask` implements `Runnable` (rather than extending `Thread`) for greater flexibility

### 2. Static vs Dynamic Resource Routing

```java
// Routing logic in page03 MyTask.run()
if (requestModel contains "." || requestModel.equals("/")) {
    // Static resource → StaticResourceHandler
} else {
    // Dynamic resource → switch(requestModel) → matching Servlet
}
```

**The criterion**: whether the URL contains a file extension. `/login`, `/enroll` have no `.` — dispatched to a Servlet. `/333.jpg`, `/index.html` contain `.` — served as static resources.

### 3. BaseServlet Dispatch Pattern

```
handlerServlet(request, response)
  │
  ├── GET  ──▶ doGet(request, response)
  └── POST ──▶ doPost(request, response)
```

- `BaseServlet` is an abstract class that defines `doGet` / `doPost` as abstract methods
- Subclasses (`LoginServlet`, `EnrollServlet`) override these two methods
- Callers only need `servlet.handlerServlet(req, res)` — no need to worry about the HTTP method

> This is the mini version of Tomcat's `HttpServlet.service()` → `doGet()` / `doPost()` pattern.

### 4. MVC Three-Tier Architecture

```
┌──────────────────────────────────────┐
│  Servlet layer (presentation)         │
│  LoginServlet / EnrollServlet         │
│  Role: receive params → call service  │
│        → output result                │
├──────────────────────────────────────┤
│  Service layer (business logic)       │
│  UserServiceImpl                      │
│  Role: business decisions → call DAO  │
│        → wrap result                  │
├──────────────────────────────────────┤
│  DAO layer (data access)              │
│  UserDaoImpl                          │
│  Role: JDBC database operations       │
├──────────────────────────────────────┤
│  entity / dto (data models)           │
│  StudentDO / ResponseDTO              │
└──────────────────────────────────────┘
```

**Return format at each layer**:
- DAO returns raw types or entities (`StudentDO`, `Integer`)
- Service calls DAO then wraps the result as a `ResponseDTO` (unified `{code, msg, data}` format)
- Servlet retrieves params + calls Service + serializes the ResponseDTO to JSON via fastjson2

## Page Evolution

### page01 — Thread Basics

**Key files**: [MyThread.java](src/org/gao/page01/MyThread.java), [MyRunnable.java](src/org/gao/page01/MyRunnable.java)

- `MyThread`: extends `Thread`, overrides `run()`, starts with `start()`
- `MyRunnable`: implements `Runnable`, starts via `new Thread(runnable).start()`
- Both create two threads that print numbers concurrently

> A pure demo of the two ways to create threads in Java — no HTTP involved yet. After understanding this, page02 makes it clear why `MyTask implements Runnable`.

### page02 — Multi-Threading + Static Resources

**Key files**: [MyServer.java](src/org/gao/page02/MyServer.java), [MyTask.java](src/org/gao/page02/MyTask.java)

- `MyServer`: `while(true)` accept then `new Thread(new MyTask(socket)).start()`
- `MyTask`: implements `Runnable`, reuses the parse/response logic from module 02 inside `run()`
- Only handles static resources (read files, return 404)

> Essentially wraps the 02.StaticResourceHandler code inside a thread. One connection per thread — requests never block each other.

### page03 — Full MVC (Final Version)

**Key files**: 21 files across 6 layers

| Layer | Files |
|-------|-------|
| Entry + Routing | `MyServer.java`, `MyTask.java` |
| Protocol Handling | `MyHttpRequest.java`, `MyHttpResponse.java` |
| Static Resources | `StaticResourceHandler.java` |
| Presentation | `BaseServlet.java`, `LoginServlet.java`, `EnrollServlet.java` |
| Business Logic | `IUserService.java`, `UserServiceImpl.java` |
| Data Access | `IUserDAO.java`, `UserDaoImpl.java` |
| Data Models | `StudentDO.java`, `ResponseDTO.java` |

Complete request flow (using `/login` as an example):

```
Browser POST /login?account=admin&password=123456
  │
  ▼
MyServer.accept() → new Thread(MyTask).start()
  │
  ▼
MyTask.run()
  ├── new MyHttpRequest(requestMsg)         // Parse message
  ├── requestModel does not contain "."      // Dynamic resource
  ├── switch: "/login"
  └── new LoginServlet().handlerServlet()
        │
        ├── BaseServlet sees POST → doPost()
        │
        ▼
      LoginServlet.doPost()
        ├── httpRequest.getRequestParamToKey("account")  // "admin"
        ├── httpRequest.getRequestParamToKey("password")  // "123456"
        ├── new UserServiceImpl().login(acc, pwd)
        │     ├── new UserDaoImpl().login(acc, pwd)
        │     │     └── JDBC select → StudentDO / null
        │     └── Wrap as ResponseDTO
        └── httpResponse.write(JSON.toJSONBytes(dto))
```

**Key changes from page02**:

| Change | Description |
|--------|-------------|
| MyTask routing split | URL with "." → static; without → dynamic switch |
| BaseServlet pattern | Abstract GET/POST dispatch; subclasses only override doGet/doPost |
| MVC three-tier | servlet → service → dao, each layer with clear responsibilities |
| ResponseDTO | Unified `{code, msg, data}` response format |
| JDBC database | Raw DriverManager + PreparedStatement connecting to MySQL |
| fastjson2 | JSON serialization of ResponseDTO output |

**MyHttpRequest / MyHttpResponse enhancements**:
- `MyHttpRequest` adds `getRequestParamToKey(key)` for easier param access from Servlets
- `MyHttpResponse` adds `write(byte[] bytes)` overload (defaults to `text/html`)

## Key Code Guide

| File | Lines | Key Point |
|------|-------|-----------|
| [page01/MyThread.java:21-22](src/org/gao/page01/MyThread.java) | 21-22 | Extend Thread → new → start() |
| [page01/MyRunnable.java:31-32](src/org/gao/page01/MyRunnable.java) | 31-32 | Implement Runnable → new Thread(runnable).start() |
| [page02/MyServer.java:27](src/org/gao/page02/MyServer.java) | 27 | `while(true)` accept → `new Thread(MyTask).start()` |
| [page02/MyTask.java:26-52](src/org/gao/page02/MyTask.java) | 26-52 | Full request processing flow inside Runnable.run() |
| [page03/MyTask.java:36-39](src/org/gao/page03/MyTask.java) | 36-39 | Static/dynamic resource decision: `requestModel.contains(".")` |
| [page03/MyTask.java:46-50](src/org/gao/page03/MyTask.java) | 46-50 | switch routing to LoginServlet / EnrollServlet |
| [page03/BaseServlet.java:19-25](src/org/gao/page03/servlet/BaseServlet.java) | 19-25 | `handlerServlet()` dispatches GET/POST to doGet/doPost |
| [page03/LoginServlet.java:28-32](src/org/gao/page03/servlet/LoginServlet.java) | 28-32 | Get params → call Service → serialize to JSON |
| [page03/ResponseDTO.java:22-23](src/org/gao/page03/dto/ResponseDTO.java) | 22-23 | Factory methods `success()` / `error()` for unified responses |
| [page03/UserDaoImpl.java:29-41](src/org/gao/page03/dao/impl/UserDaoImpl.java) | 29-41 | Static block loading druid.properties for driver and connection config |
| [page03/UserDaoImpl.java:46-59](src/org/gao/page03/dao/impl/UserDaoImpl.java) | 46-59 | try-with-resources JDBC: Connection → PreparedStatement → ResultSet |

## Mapping to Real Tomcat

| This Module | Tomcat Concept | Notes |
|-------------|---------------|-------|
| `while(true) + new Thread()` | Connector + Executor | Tomcat uses a thread pool; same concept |
| `MyTask` (Runnable) | `SocketProcessor` | Encapsulates processing for a single request |
| `BaseServlet.handlerServlet()` | `HttpServlet.service()` | The core GET/POST dispatch pattern |
| `LoginServlet` / `EnrollServlet` | Custom Servlet | User-written business Servlets |
| `switch(requestModel)` routing | `Mapper.map()` | URL → Servlet mapping |
| `ResponseDTO` | `HttpServletResponse` | Unified response format |
| `UserDaoImpl` (JDBC) | JPA / MyBatis | Tomcat doesn't manage DAOs, but real projects always include this layer |

## How to Run

### page01

1. Run `MyThread.java` or `MyRunnable.java` directly
2. The console prints alternating output from both threads

### page02

1. Start `MyServer.java`
2. Open `http://localhost:9090/333.jpg` in a browser
3. Open multiple browser tabs simultaneously to observe concurrent handling

### page03

1. Ensure MySQL is running, then execute [config/init.sql](../config/init.sql)
2. Start `MyServer.java`
3. Use Postman or a browser to test:
   - Static: `http://localhost:9090/333.jpg`
   - Login: `POST http://localhost:9090/login?account=admin&password=123456`
   - Register: `POST http://localhost:9090/enroll?account=new&password=123&name=NewUser`
4. Returns JSON: `{"code":1, "msg":"success", "data":{...}}`

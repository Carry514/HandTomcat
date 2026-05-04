# HandTomcat — A Handwritten Mini Tomcat

Build a mini Tomcat container from scratch using pure Java. Understand how HTTP servers work under the hood.

## Overview

Tomcat is the most widely used Servlet container in Java web development, but its source code is vast and complex. This project implements Tomcat's core mechanisms with minimal code through **progressive evolution**, helping you understand:

- HTTP protocol parsing (request/response messages)
- How a Servlet container works
- Multi-threaded request handling
- Annotation scanning and route mapping
- Database connection pool integration

## Learning Path

```
01.Socket              → Basic Socket communication
02.StaticResource      → HTTP protocol parsing + static resource serving
03.Thread              → Multi-threading + MVC three-tier architecture
04.DynamicResource     → Servlet mechanism + annotation-based routing
05.DBPool              → Connection pool + thread pool, the final version
```

Each module contains multiple `page` subdirectories that demonstrate the evolution from crude to refined. Read them in order.

## Project Structure

```
HandTomcat/
├── 01.Socket/                  # Module 1: Socket basics
│   └── src/org/gao/page01~05/  # 5 evolution steps
├── 02.StaticResourceHandler/   # Module 2: Static resource handling
│   └── src/org/gao/page01~03/
├── 03.Thread/                  # Module 3: Multi-threading support
│   └── src/org/gao/page01~03/
├── 04.DynamicResource/         # Module 4: Dynamic resources (Servlet)
│   └── src/org/gao/page01~04/
├── 05.DBPool/                  # Module 5: Database connection pool
│   └── src/org/gao/page01~02/
├── config/                     # Configuration files
│   ├── servlet.properties      # Servlet route mappings
│   └── druid.properties        # Database connection pool config
└── webapps/                    # Web static resources
    ├── pages/                  # HTML pages
    └── static/images/          # Image resources
```

## Environment Setup

### Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| JDK | 1.8+ | For compilation and runtime |
| IntelliJ IDEA | Any version | Open the project and auto-detect multi-module structure |

### Database (Required for modules 03~05)

1. Install MySQL and create the database:
   ```sql
   CREATE DATABASE handtomcat;
   ```
2. Run [config/init.sql](config/init.sql) to create tables and insert test data
3. Update the database connection info in [config/druid.properties](config/druid.properties):
   ```properties
   url=jdbc:mysql://localhost:3306/handtomcat
   username=your_username
   password=your_password
   ```

### Import the Project

1. Use IntelliJ IDEA, select **Open**, and choose the project root directory
2. IDEA will automatically recognize each module's `.iml` configuration
3. JAR files in each module's `lib/` directory need to be manually added as module dependencies (or ignore compilation errors and just read the source)

## Quick Start

1. Open the project root directory with IntelliJ IDEA
2. Start from `01.Socket` and read the modules in order
3. `page01` is the simplest version in each module; `page0N` is the final version
4. Run any `APP.java` or `MyServer.java` to start the corresponding server

> Modules 03~05 require a MySQL database. Run the SQL script first and update `config/druid.properties`.

## Core Architecture (Final Version)

```
Browser request :9090
      │
      ▼
┌─────────────┐
│   APP.java  │  ServerSocket(9090) + ThreadPoolExecutor
└──────┬──────┘
       │ accept()
       ▼
┌─────────────┐
│  MyTask.java │  Runnable task, parses request + dispatches to routes
└──────┬──────┘
       │
       ├── @ServletMapping matched ──▶ LoginServlet / EnrollServlet etc.
       │
       └── No match ────────────────▶ DefaultServlet → StaticResourceHandler
                                          │
                                          ▼
                               Static files under webapps/
```

## Dependencies

The project uses raw JDBC plus the following third-party libraries:

| Library | Purpose | Used By |
|---------|---------|---------|
| fastjson2 | JSON serialization | 03~05 |
| mysql-connector-j | MySQL driver | 03~05 |
| druid | Database connection pool | 04~05 |
| reflections | Classpath annotation scanning | 04~05 |
| guava | General utilities | 05 |
| javassist | Bytecode manipulation | 05 |

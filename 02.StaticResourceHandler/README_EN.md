# 02.StaticResourceHandler — Static Resource Handling

## Learning Objectives

- Master **manual parsing** of HTTP request messages (splitting request line/headers/body)
- Understand how to encapsulate parsing logic into independent `MyHttpRequest` / `MyHttpResponse` classes
- Master static resource handler design: file reading + MIME type detection

## Prerequisites

Understand the ServerSocket communication model and HTTP response message structure from [01.Socket](../01.Socket/README.md).

## Core Concepts

### 1. Request Message Parsing Flow

```
Raw request message (string)
        │
        ▼
  split("\r\n")       →  Split into "request line" and remaining lines
        │
        ├── Line 0  →  split(" ")  →  [Method, URL, Protocol]
        │
        ├── Lines 1~N →  Request headers  →  split(": ")  →  [Key, Value]
        │
        └── Last line →  Request body (only for POST)
```

**Key points**:
- Request line, headers, blank line, and body are separated by `\r\n`
- GET parameters are appended to the URL, separated by `?`, with individual params joined by `&`
- POST parameters are placed in the request body, with the same format as GET parameters

### 2. Class Responsibility Separation

The core evolution of this module is splitting "one big blob of code" into three classes with clear responsibilities:

```
┌─────────────────┐
│  MyHttpRequest  │  → Parse raw message → extract method / URL / params / headers / body
├─────────────────┤
│ StaticResource  │  → Given a file path → read bytes + detect MIME type
│    Handler      │
├─────────────────┤
│ MyHttpResponse  │  → Unified output interface → write(media, bytes)
└─────────────────┘
```

This maps to the architectural prototype of `Request` → `ResourceHandler` → `Response` in real Tomcat.

**Why split?** In one phrase: **encapsulate change, separate concerns.**

- `MyServer`'s ideal role is a "dispatcher" — accept requests, locate resources, write responses. If it also has to split strings, check for question marks, split `&` and `=`, it becomes a "dispatcher + protocol parser" with muddled responsibilities
- **Reusability**: Modules 03~05 all reuse `MyHttpRequest` — no need to rewrite parsing logic
- **Independent evolution**: When page02 → page03 adds header parsing, only `MyHttpRequest` changes; `MyServer` is untouched
- **Mirrors real design**: Tomcat's `HttpServletRequest` (parsing requests) and `HttpServletResponse` (building responses) follow the exact same philosophy — each encapsulates its own protocol details

### 3. MIME Type Detection

Media types are determined by file extension:

```java
String suffix = filePath.split("\\.")[last];
if (suffix.equals("html")) → text/html
if (suffix.equals("jpg"))  → image/jpg
if (suffix.equals("png"))  → image/png
```

Later modules will switch to `MimetypesFileTypeMap` for automatic detection. Here we implement it manually to understand the principle.

## Page Evolution

### page01 — Manual Request Line Parsing

**Key files**: [MyServer.java](src/org/gao/page01/MyServer.java)

- Uses `split("\r\n")` to break apart the request, taking the first line as the request line
- Extracts the URL from the request line and builds the local path as `webapps/static/images` + URL
- Uses `FileNotFoundException` as a fallback for 404
- Determines MIME type by file extension (html / jpg / png)

> All logic lives in one class. It works, but parsing, file reading, and response writing are all coupled together.

### page02 — Extracting MyHttpRequest

**Key files**: [MyServer.java](src/org/gao/page02/MyServer.java), [MyHttpRequest.java](src/org/gao/page02/MyHttpRequest.java)

- Introduces the `MyHttpRequest` class — pass the raw message string to the constructor and it auto-parses
- Parsing capabilities: request method, URL, protocol version, GET parameters (`?` split + `&` split), POST request body
- `MyServer` no longer manually splits; it calls `httpRequest.getRequestURL()` to get the path
- `praseRequestHead()` is declared but not yet implemented — left for page03

### page03 — Full Class Separation (Final Version)

**Key files**: [MyServer.java](src/org/gao/page03/MyServer.java), [MyHttpRequest.java](src/org/gao/page03/MyHttpRequest.java), [MyHttpResponse.java](src/org/gao/page03/MyHttpResponse.java), [StaticResourceHandler.java](src/org/gao/page03/StaticResourceHandler.java)

The four classes and their responsibilities:

| Class | Responsibility | Key Methods |
|-------|---------------|-------------|
| `MyHttpRequest` | Parse request messages | `praseRequestLine()` / `praseRequestHead()` / `praseRequestBody()` |
| `StaticResourceHandler` | Read files + MIME detection | `getFileByte()` / `getFileMedia()` |
| `MyHttpResponse` | Output HTTP response | `write(media, bytes)` |
| `MyServer` | Orchestration & dispatch | `while(true)` loop + route decisions |

What page03 adds over page02:
- `MyHttpRequest` gains request header parsing (iterating the array, splitting on `": "`)
- Array bounds guards added (`length > 1` check after `split`)
- `MyHttpResponse` encapsulates response output behind a unified `write(media, bytes)` interface
- `StaticResourceHandler` encapsulates file reading and MIME detection
- `MyServer` adds `while(true)` loop, root path `/` → index page, missing files → 404

## Key Code Guide

| File | Lines | Key Point |
|------|-------|-----------|
| [page01/MyServer.java:34-41](src/org/gao/page01/MyServer.java) | 34-41 | `split("\r\n")` on request → extract request line → `split(" ")` to get URL |
| [page01/MyServer.java:72-78](src/org/gao/page01/MyServer.java) | 72-78 | Using `FileNotFoundException` as 404 fallback |
| [page01/MyServer.java:86-94](src/org/gao/page01/MyServer.java) | 86-94 | MIME type by file extension |
| [page02/MyHttpRequest.java:64-83](src/org/gao/page02/MyHttpRequest.java) | 64-83 | Constructor → `praseRequsetMSG()` → split on `\r\n` → dispatch to parsing methods |
| [page02/MyHttpRequest.java:109-128](src/org/gao/page02/MyHttpRequest.java) | 109-128 | GET param parsing: split URL on `?` → split params on `&` → split key-value on `=` |
| [page03/MyHttpRequest.java:138-148](src/org/gao/page03/MyHttpRequest.java) | 138-148 | Header parsing: iterate `splitRequestMSGArray`, split on `": "` |
| [page03/MyHttpResponse.java:26-43](src/org/gao/page03/MyHttpResponse.java) | 26-43 | Encapsulated `write()`: status line + headers + blank line + body |
| [page03/StaticResourceHandler.java:42-49](src/org/gao/page03/StaticResourceHandler.java) | 42-49 | `FileInputStream` reading file into byte array |
| [page03/StaticResourceHandler.java:56-67](src/org/gao/page03/StaticResourceHandler.java) | 56-67 | MIME type matching by file extension |

## Mapping to Real Tomcat

| This Module | Tomcat Concept | Notes |
|-------------|---------------|-------|
| `MyHttpRequest.parseRequestLine()` | `Http11Processor.parseRequestLine()` | Parse HTTP request line |
| `MyHttpRequest.parseRequestHead()` | `Http11Processor.parseHeaders()` | Parse HTTP request headers |
| `MyHttpResponse.write()` | `Http11OutputBuffer.sendResponse()` | Write HTTP response |
| `StaticResourceHandler` | `DefaultServlet` / `WebResourceRoot` | Handle static resource requests |
| Route decisions in `MyServer` | `Mapper.map()` | URL → resource mapping |

## How to Run

### page01 / page02

1. Start `MyServer.java`
2. Open `http://localhost:9090/333.jpg` in a browser
3. Place images in the `webapps/static/images/` directory

### page03

1. Start `MyServer.java`
2. Open in a browser:
   - `http://localhost:9090/` → Index page (`webapps/pages/index.html`)
   - `http://localhost:9090/333.jpg` → Image (`webapps/static/images/333.jpg`)
   - Accessing a non-existent path → 404 page

# 01.Socket — Basic Socket Communication

## Learning Objectives

- Understand the `ServerSocket` / `Socket` communication model
- Understand the structure of HTTP request messages sent by browsers
- Hand-write HTTP response messages so browsers can render content correctly

## Prerequisites

None. This is the first module of the project, starting from zero.

## Core Concepts

### 1. ServerSocket Communication Model

```
Server                              Client (Browser)
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

- `accept()` is a **blocking** method — it waits indefinitely until a client connects
- Each client connection returns an independent `Socket` object
- The `InputStream` / `OutputStream` obtained from the `Socket` enables bidirectional communication

### 2. HTTP Request Message Structure

A raw HTTP request from a browser looks like this:

```
GET /111.jpg HTTP/1.1
Host: localhost:9090
Connection: keep-alive
User-Agent: Mozilla/5.0 ...
Accept: image/avif,image/webp,...

```

- **Request Line**: `Method Path ProtocolVersion`
- **Request Headers**: `Key: Value` pairs
- **Request Body**: Usually empty for GET requests

### 3. HTTP Response Message Structure

```text
HTTP/1.1 200 OK\r\n                  ← Status line
Content-Type: text/html;charset=UTF-8\r\n  ← Response header
Content-Length: 6\r\n                ← Response header
\r\n                                  ← Blank line (separator)
<html>...</html>                      ← Response body
```

**Key point**: There must be a blank line (`\r\n\r\n`) between headers and body. The browser uses this blank line to distinguish headers from content.

### 4. MIME Types

The browser decides how to render content based on `Content-Type`:

| Content-Type | Effect |
|---|---|
| `text/html` | Render as web page |
| `image/png` | Display as image |
| `image/jpeg` | Display as image |
| `application/octet-stream` | Trigger download |

## Page Evolution

### page01 — Minimal Communication

**Key files**: [MyServer.java](src/org/gao/page01/MyServer.java), [MyClient.java](src/org/gao/page01/MyClient.java)

- Server calls `accept()` once, reads bytes from the client, prints to console
- Client sends a hardcoded string
- **Takeaway**: Understand the chain `ServerSocket → accept → Socket → InputStream`

### page02 — Loop Send & Receive

**Key files**: [MyServer.java](src/org/gao/page02/MyServer.java), [MyClient.java](src/org/gao/page02/MyClient.java)

- Server adds `while(true)` for continuous reading; client adds `Scanner` for continuous input
- **Limitation**: Receives but never replies — a browser would hang indefinitely

### page03 — First HTTP Response

**Key files**: [MyServer.java](src/org/gao/page03/MyServer.java)

- Access `http://localhost:9090` in a browser
- Manually constructs the status line `HTTP/1.1 200 OK`, headers `Content-Type` and `Content-Length`, and returns the text "你好"
- **Takeaway**: Understand HTTP response message format, especially the role of the **blank line separator**

### page04 — Serving Images

**Key files**: [MyServer.java](src/org/gao/page04/MyServer.java)

- Reads a local image file `webapps/static/images/333.jpg`
- Sets `Content-Type: image/png` and writes the image bytes to the response body
- **Note**: This is a demonstration of the basic image serving flow. Limitations like hardcoded Content-Type and single-shot handling will be addressed in subsequent pages

### page05 — Dynamic MIME + Complete HTTP Server

**Key files**: [MyServer.java](src/org/gao/page05/MyServer.java), [MyHttpServer.java](src/org/gao/page05/MyHttpServer.java)

- `MyServer.java`: Uses `MimetypesFileTypeMap` to auto-detect file MIME types
- `MyHttpServer.java`: The **final version** of this module, featuring:
  - `while(true)` loop for continuous request handling
  - Request line parsing to extract the request path
  - File extension → MIME mapping (jpg/png/gif)
  - 404/400/403 error handling
  - Path traversal attack protection (`normalize()` validation)

## Key Code Guide

| File | Lines | Key Point |
|------|-------|-----------|
| [page01/MyServer.java:20-23](src/org/gao/page01/MyServer.java) | 20-23 | `ServerSocket(9090)` creation → `accept()` blocking wait |
| [page03/MyServer.java:37-44](src/org/gao/page03/MyServer.java) | 37-44 | Hand-writing HTTP response — **note the blank line `\r\n\r\n`** |
| [page04/MyServer.java:32-35](src/org/gao/page04/MyServer.java) | 32-35 | `FileInputStream` reading a file into a byte array |
| [page05/MyHttpServer.java:48-54](src/org/gao/page05/MyHttpServer.java) | 48-54 | Extracting path from request line `GET /aaa.jpg HTTP/1.1` via split |
| [page05/MyHttpServer.java:57-63](src/org/gao/page05/MyHttpServer.java) | 57-63 | Path traversal protection — preventing `../../etc/passwd` attacks |
| [page05/MyHttpServer.java:91-103](src/org/gao/page05/MyHttpServer.java) | 91-103 | Encapsulated `sendResponse()` method for building complete HTTP responses |

## Mapping to Real Tomcat

| This Module | Tomcat Concept | Notes |
|-------------|---------------|-------|
| `ServerSocket(9090)` | Connector Endpoint | Low-level port listening |
| `socket.accept()` | Acceptor thread | Waits for and accepts new connections |
| `requestLine.split(" ")` | Request parsing | Done by `Http11Processor` in Tomcat |
| `sendResponse()` | Response output | Done by `Http11OutputBuffer` in Tomcat |
| `while(true)` loop | Event loop | Tomcat uses NIO Selector; the concept is the same |

## How to Run

### page01 — Minimal Communication

1. Start `MyServer.java`
2. Then start `MyClient.java` — the message is hardcoded and sent directly

### page02 — Loop Send & Receive

1. Start `MyServer.java`
2. Then start `MyClient.java` and type messages in the console

> Page01/02 only receive, never reply. A browser would hang — use MyClient instead.

### page03 — Browser Access

1. Start `MyServer.java`
2. Open `http://localhost:9090` in a browser
3. The page displays "你好"

### page04 — Serving Images

1. Start `MyServer.java`
2. Open `http://localhost:9090` in a browser
3. The page displays the image `webapps/static/images/333.jpg`

### page05 — Complete HTTP Server

1. Start `MyServer.java` or `MyHttpServer.java`
2. Place images in the `webapps/static/images/` directory
3. Open `http://localhost:9090/<image-name>` in a browser
   - Example: `http://localhost:9090/333.jpg`
4. Returns a 404 page if the file does not exist

> All static resources (images, HTML, etc.) should be placed under the project root's `webapps/` directory.

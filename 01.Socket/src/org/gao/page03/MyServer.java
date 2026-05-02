package org.gao.page03;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao
 * @Author: 高
 * @CreateTime: 2025-06-15 15:12
 * @Description: 演示 HTTP 响应报文结构，向浏览器返回文本内容
 * @Version: 1.0
 */
public class MyServer {
    public static void main(String[] args) throws IOException {

        //1.建立与客户端的通信管道
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("服务端启动了，端口为：9090");
        //等待客户端连接(无连接会一直阻塞程序)有客户端连接会返回客户端对象
        Socket socket = serverSocket.accept();
        //通过Socket获取InputStream
        InputStream is = socket.getInputStream();

        byte[] bytes = new byte[1024];
        int len = is.read(bytes);

        System.out.println(new String(bytes,0,len));//打印请求标头

        //给浏览器响应消息
        String msg = "你好";

        OutputStream os = socket.getOutputStream();
        os.write("HTTP/1.1 200 OK".getBytes());
        os.write("\r\n".getBytes());
        os.write("Content-Type: text/html;charset=UTF-8".getBytes());
        os.write("\r\n".getBytes());
        os.write(("Content-Length:" + msg.getBytes().length).getBytes());
        os.write("\r\n".getBytes());
        os.write("\r\n".getBytes());
        os.write(msg.getBytes());
    }
}

package org.gao.page04;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao
 * @Author: 高
 * @CreateTime: 2025-06-15 15:12
 * @Description: 演示图片文件读取与响应，向浏览器返回图片数据
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

        System.out.println(new String(bytes,0,len));

        //给浏览器响应图片数据
        FileInputStream fis = new FileInputStream("webapps/static/images/333.jpg");
        System.out.println("文件的大小为:" + fis.available());
        byte[] bytes1 = new byte[fis.available()];
        fis.read(bytes1);


        OutputStream os = socket.getOutputStream();
        //响应行
        os.write("HTTP/1.1 200 OK".getBytes());
        os.write("\r\n".getBytes());
        //响应头
        os.write("Content-Type: image/png;charset=UTF-8".getBytes());
        os.write("\r\n".getBytes());
        os.write(("Content-Length:" + bytes1.length).getBytes());
        os.write("\r\n".getBytes());
        os.write("\r\n".getBytes());
        //响应体
        os.write(bytes1);

    }
}

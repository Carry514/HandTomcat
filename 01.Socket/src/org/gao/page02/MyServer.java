package org.gao.page02;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao
 * @Author: 高
 * @CreateTime: 2025-06-15 15:12
 * @Description: 演示 while(true) 循环，持续接收客户端消息
 * @Version: 1.0
 */
public class MyServer {
    public static void main(String[] args) throws IOException {

        //1.建立与客户端的通信管道
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("服务端启动了，端口为：9090");
        //等待客户端连接(无连接会一直阻塞程序)有客户端连接会返回客户端对象
        Socket socket = serverSocket.accept();

        while(true){
            //通过Socket获取InputStream
            InputStream is = socket.getInputStream();

            byte[] bytes = new byte[1024];
            int len = is.read(bytes);

            System.out.println(new String(bytes,0,len));
        }

    }
}

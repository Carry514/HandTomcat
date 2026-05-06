package org.gao.page02;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao
 * @Author: 高
 * @CreateTime: 2025-06-21 16:32
 * @Description: 多线程服务端，while(true) accept 后为每个连接创建新线程
 * @Version: 1.0
 */
public class MyServer {
    public static void main(String[] args) throws IOException {

        //1.建立与客户端的通信管道
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("服务端启动了，端口为：9090");

        while (true){
            // 等待客户端连接(无连接会一直阻塞程序)有客户端连接会返回客户端对象
            Socket socket = serverSocket.accept();

            // 来一个客户端 开一个线程
            new Thread(new MyTask(socket)).start();
        }

    }
}

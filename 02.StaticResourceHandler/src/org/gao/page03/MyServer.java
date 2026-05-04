package org.gao.page03;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao
 * @Author: 高
 * @CreateTime: 2025-06-21 16:32
 * @Description: 整合 MyHttpRequest + MyHttpResponse + StaticResourceHandler，实现完整 HTTP 静态服务
 * @Version: 1.0
 */
public class MyServer {
    public static void main(String[] args) throws IOException {

        //1.建立与客户端的通信管道
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("服务端启动了，端口为：9090");
        //等待客户端连接(无连接会一直阻塞程序)有客户端连接会返回客户端对象
        Socket socket = serverSocket.accept();

        while(true) {
            //通过Socket获取InputStream
            InputStream is = socket.getInputStream();

            byte[] bytes = new byte[1024];
            int len = is.read(bytes);

            // 获取请求消息
//        System.out.println(new String(bytes,0,len)); // requestMessage
            String requestMsg = new String(bytes, 0, len);
            // 打印请求消息
//        System.out.println(requestMsg);

            // 解析消息
            MyHttpRequest httpRequest = new MyHttpRequest(requestMsg);

//            http://127.0.0.1:9090/static/images/333.jpg
//            http://127.0.0.1:9090/pages/index.html
            String filePath = "webapps" + httpRequest.getRequestURL();

            File file = new File(filePath);


            // 127.0.0.1:9090/   只访问ip:端口 响应首页
            if(httpRequest.getRequestURL().equals("/")){
                filePath = "webapps/pages/index.html";
                // 1.文件不存在 响应404
            } else if (!file.exists()) {
                filePath = "webapps/pages/404.html";
            }

            StaticResourceHandler staticResourceHandler = new StaticResourceHandler(filePath);

            MyHttpResponse myHttpResponse = new MyHttpResponse(socket);

            myHttpResponse.write(staticResourceHandler.getMedia(),staticResourceHandler.getFileBytes());

        }
    }
}

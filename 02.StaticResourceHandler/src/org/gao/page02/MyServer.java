package org.gao.page02;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao
 * @Author: 高
 * @CreateTime: 2025-06-21 16:32
 * @Description: 引入 MyHttpRequest 封装请求解析，提取 URL 并返回对应资源
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

        // 获取请求消息
//        System.out.println(new String(bytes,0,len)); // requestMessage
        String requestMsg = new String(bytes,0,len);
        System.out.println(requestMsg);

        // 解析消息
        MyHttpRequest httpRequest = new MyHttpRequest(requestMsg);

        String requestURL = httpRequest.getRequestURL();


        // 给浏览器响应图片数据
        byte[] bytes1 = null;
        try {
            FileInputStream fis = new FileInputStream("webapps/static/images" + requestURL);
            System.out.println("文件的大小为:" + fis.available());
            bytes1 = new byte[fis.available()];
            fis.read(bytes1);

        } catch (FileNotFoundException e) {
            // 如果报文件找不到异常
            requestURL = "/404.html";
            FileInputStream fis = new FileInputStream("webapps/static/images" + requestURL);
            System.out.println("文件的大小为:" + fis.available());
            bytes1 = new byte[fis.available()];
            fis.read(bytes1);
        }


        // 获取媒体类型
        String media = "";

        // 切割requestURL按.切割
        String[] splitRequestURL = requestURL.split("\\.");

        if(splitRequestURL[1].equals("html")){
            media = "text/html";
        }else if(splitRequestURL[1].equals("jpg")){
            media = "image/jpg";
        }else if(splitRequestURL[1].equals("png")){
            media = "image/png";
        }

//        System.out.println(Arrays.toString(splitRequestURL));



        OutputStream os = socket.getOutputStream();
        //响应行
        os.write("HTTP/1.1 200 OK".getBytes());
        os.write("\r\n".getBytes());
        //响应头
        os.write(("Content-Type: " + media + ";charset=UTF-8").getBytes());
        os.write("\r\n".getBytes());
        os.write(("Content-Length:" + bytes1.length).getBytes());
        os.write("\r\n".getBytes());
        os.write("\r\n".getBytes());
        //响应体
        os.write(bytes1);

    }
}

package org.gao.page01;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao
 * @Author: 高
 * @CreateTime: 2025-06-15 15:22
 * @Description: 演示 Socket 基本用法，向服务端发送消息
 * @Version: 1.0
 */
public class MyClient {
    public static void main(String[] args) throws IOException {
        //1.建立连接
        Socket socket = new Socket("127.0.0.1", 9090);

        OutputStream os = socket.getOutputStream();
        //写出数据
        os.write("今天周五有什么安排".getBytes());//字符串转为字节数组
    }
}

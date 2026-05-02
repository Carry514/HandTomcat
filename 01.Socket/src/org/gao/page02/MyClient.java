package org.gao.page02;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao
 * @Author: 高
 * @CreateTime: 2025-06-15 15:22
 * @Description: 演示 while(true) 循环，持续向服务端发送消息
 * @Version: 1.0
 */
public class MyClient {
    public static void main(String[] args) throws IOException {
        //1.建立连接
        Socket socket = new Socket("127.0.0.1", 9090);

        Scanner sc = new Scanner(System.in);
        OutputStream os = socket.getOutputStream();

        while(true){
            System.out.println("请输入要发送给服务端的消息：");
            String next = sc.next();

            //写出数据
            os.write(next.getBytes());//字符串转为字节数组
            System.out.println("已发送：" + next);
        }
    }
}

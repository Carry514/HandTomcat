package org.gao.page03;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page03
 * @Author: 高
 * @CreateTime: 2025-06-27 13:44
 * @Description: 响应封装
 * @Version: 1.0
 */
public class MyHttpResponse {

    private Socket socket;


    public MyHttpResponse(Socket socket) {
        this.socket = socket;
    }


    public void write(byte[] bytes){
        this.write("text/html" , bytes);
    }

//    如果是其他资源？ --> 动态资源
    public void write(String media,byte[] bytes){
        try {
            OutputStream os = socket.getOutputStream();
            //响应行
            os.write("HTTP/1.1 200 OK".getBytes());
            os.write("\r\n".getBytes());
            //响应头
            os.write(("Content-Type: " + media + ";charset=UTF-8").getBytes());
            os.write("\r\n".getBytes());
            os.write(("Content-Length:" + bytes.length).getBytes());
            os.write("\r\n".getBytes());
            os.write("\r\n".getBytes());
            //响应体
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

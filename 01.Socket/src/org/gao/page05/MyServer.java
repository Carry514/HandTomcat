package org.gao.page05;

import javax.activation.MimetypesFileTypeMap;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page05
 * @Author: 高
 * @CreateTime: 2025-06-15 21:14
 * @Description: 演示 MimetypesFileTypeMap 自动探测文件 MIME 类型
 * @Version: 1.0
 */
public class MyServer {

    private static final String BASE_DIR = "webapps/static/images";

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("服务端启动了，端口为9090");
        //等待连接
        Socket socket = serverSocket.accept();

        InputStream is = socket.getInputStream();

        byte[] bytes = new byte[1024];
        int len = is.read(bytes);
        System.out.println(new String(bytes,0,len));

        //给浏览器响应图片数据
        //文件地址
        String file = "webapps/static/images/333.jpg";

        FileInputStream fis = new FileInputStream(file);
        System.out.println("文件的大小为:" + fis.available());
        byte[] bytes1 = new byte[fis.available()];
        fis.read(bytes1);

        String type = new MimetypesFileTypeMap().getContentType(file);//获取图片格式
        System.out.println(type);//打印图片格式

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

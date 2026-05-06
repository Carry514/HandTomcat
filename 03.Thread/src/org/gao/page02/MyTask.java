package org.gao.page02;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page02
 * @Author: 高
 * @CreateTime: 2025-06-27 15:27
 * @Description: 任务对象
 * @Version: 1.0
 */
public class MyTask implements Runnable{

    private Socket socket;

    public MyTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            while(true) {
                //通过Socket获取InputStream
                InputStream is = socket.getInputStream();

                byte[] bytes = new byte[1024];
                int len = is.read(bytes);
                if (len == -1) {
                    break;
                }

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

                // 响应
                MyHttpResponse myHttpResponse = new MyHttpResponse(socket);
                myHttpResponse.write(staticResourceHandler.getMedia(),staticResourceHandler.getFileBytes());

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}

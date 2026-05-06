package org.gao.page03;

import org.gao.page03.servlet.EnrollServlet;
import org.gao.page03.servlet.LoginServlet;

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
                // 响应
                MyHttpResponse httpResponse = new MyHttpResponse(socket);

                String requestModel = httpRequest.getRequestModel();
                // 如何判断是动态请求 还是静态请求？
                // 判断请求模块有没有. 如果有则是静态资源
                if (requestModel != null && (requestModel.contains(".") || requestModel.equals("/"))){
                    // 静态资源

                    String filePath = "webapps" + httpRequest.getRequestURL();

                    File file = new File(filePath);


                    // 127.0.0.1:9090/   只访问ip:端口 响应首页
                    if(httpRequest.getRequestURL().equals("/")){
                        filePath = "webapps/pages/index.html";
                        // 1.文件不存在 响应404
                    } else if (!file.exists()) {
                        filePath = "webapps/pages/404.html";
                    }
                    // 静态资源处理器
                    StaticResourceHandler staticResourceHandler = new StaticResourceHandler(filePath);
                    httpResponse.write(staticResourceHandler.getMedia(),staticResourceHandler.getFileBytes());

                } else {
                    // 动态资源
                    switch (httpRequest.getRequestModel() != null ? httpRequest.getRequestModel() : ""){
                        case "/login":
                            new LoginServlet().handlerServlet(httpRequest,httpResponse);
                            break;
                        case "/enroll":
                            new EnrollServlet().handlerServlet(httpRequest,httpResponse);
                            break;
                        case "/getUser":
                            System.out.println("获取用户信息");
                            break;
                        case "/updatePWD":
                            System.out.println("修改密码");
                            break;
                        default:
                            // 404
                            break;
                    }
                }

                System.out.println(httpRequest.getRequestModel());

    //            http://127.0.0.1:9090/static/images/333.jpg
    //            http://127.0.0.1:9090/pages/index.html



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

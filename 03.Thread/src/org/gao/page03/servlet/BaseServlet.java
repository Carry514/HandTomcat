package org.gao.page03.servlet;

import org.gao.page03.MyHttpRequest;
import org.gao.page03.MyHttpResponse;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page03.servlet
 * @Author: 高
 * @CreateTime: 2025-06-28 09:58
 * @Description: 表现层父类
 * @Version: 1.0
 */
public abstract class BaseServlet {

    public abstract void doGet(MyHttpRequest httpRequest, MyHttpResponse httpResponse);

    public abstract void doPost(MyHttpRequest httpRequest, MyHttpResponse httpResponse);

    public void handlerServlet(MyHttpRequest httpRequest, MyHttpResponse httpResponse){
        if(httpRequest.getRequestMethod().equals("GET")){
            doGet(httpRequest,httpResponse);
        } else if(httpRequest.getRequestMethod().equals("POST")){
            doPost(httpRequest,httpResponse);
        }
    }
}

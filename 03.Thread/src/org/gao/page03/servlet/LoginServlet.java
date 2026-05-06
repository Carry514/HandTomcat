package org.gao.page03.servlet;

import com.alibaba.fastjson2.JSON;
import org.gao.page03.MyHttpRequest;
import org.gao.page03.MyHttpResponse;
import org.gao.page03.dto.ResponseDTO;
import org.gao.page03.service.IUserService;
import org.gao.page03.service.impl.UserServiceImpl;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page03
 * @Author: 高
 * @CreateTime: 2025-06-27 17:39
 * @Description: 登录表现层
 * @Version: 1.0
 */
public class LoginServlet extends BaseServlet{
    @Override
    public void doGet(MyHttpRequest httpRequest, MyHttpResponse httpResponse) {
        doPost(httpRequest, httpResponse);
    }

    @Override
    public void doPost(MyHttpRequest httpRequest, MyHttpResponse httpResponse) {
        String acc = httpRequest.getRequestParamToKey("account");
        String pwd = httpRequest.getRequestParamToKey("password");

        IUserService userService = new UserServiceImpl();

        ResponseDTO dto = userService.login(acc, pwd);

        httpResponse.write(JSON.toJSONBytes(dto));

    }
}

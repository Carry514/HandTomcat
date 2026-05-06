package org.gao.page03.servlet;

import com.alibaba.fastjson2.JSON;
import org.gao.page03.MyHttpRequest;
import org.gao.page03.MyHttpResponse;
import org.gao.page03.dto.ResponseDTO;
import org.gao.page03.service.IUserService;
import org.gao.page03.service.impl.UserServiceImpl;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page03.servlet
 * @Author: 高
 * @CreateTime: 2025-06-28 09:43
 * @Description: 注册表现层
 * @Version: 1.0
 */
public class EnrollServlet extends BaseServlet{
    @Override
    public void doGet(MyHttpRequest httpRequest, MyHttpResponse httpResponse) {
        doPost(httpRequest, httpResponse);
    }

    @Override
    public void doPost(MyHttpRequest httpRequest, MyHttpResponse httpResponse) {

        String acc = httpRequest.getRequestParamToKey("account");
        String pwd = httpRequest.getRequestParamToKey("password");
        String name = httpRequest.getRequestParamToKey("name");

        IUserService userService = new UserServiceImpl();

        ResponseDTO dto = userService.enroll(acc, pwd, name);

        httpResponse.write(JSON.toJSONBytes(dto));

    }

}

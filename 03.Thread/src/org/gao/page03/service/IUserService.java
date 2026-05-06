package org.gao.page03.service;

import org.gao.page03.dto.ResponseDTO;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page03.service
 * @Author: 高
 * @CreateTime: 2025-06-27 17:50
 * @Description: 用户服务接口，返回统一响应格式 ResponseDTO
 * @Version: 1.0
 */
public interface IUserService {

    /**
     * 登录
     * @param account
     * @param password
     * @return
     */
    ResponseDTO login (String account , String password);

    ResponseDTO enroll(String account , String password , String name);
}

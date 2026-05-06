package org.gao.page03.service.impl;

import org.gao.page03.dao.IUserDAO;
import org.gao.page03.dao.impl.UserDaoImpl;
import org.gao.page03.dto.ResponseDTO;
import org.gao.page03.entity.StudentDO;
import org.gao.page03.service.IUserService;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page03.service.impl
 * @Author: 高
 * @CreateTime: 2025-06-27 17:51
 * @Description: 用户服务实现，调用 DAO 完成登录/注册并包装为 ResponseDTO
 * @Version: 1.0
 */
public class UserServiceImpl implements IUserService {
    @Override
    public ResponseDTO login(String account, String password) {

        IUserDAO userDao = new UserDaoImpl();

        StudentDO studentDO = userDao.login(account, password);

        if(studentDO != null){
            return ResponseDTO.success(studentDO);
        }

        return ResponseDTO.error("登录失败");
    }

    @Override
    public ResponseDTO enroll(String account, String password, String name) {

        UserDaoImpl userDao = new UserDaoImpl();

        Integer res = userDao.enroll(account, password, name);

        return ResponseDTO.effectResult(res);
    }
}

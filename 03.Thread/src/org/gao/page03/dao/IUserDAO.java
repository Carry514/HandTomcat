package org.gao.page03.dao;

import org.gao.page03.entity.StudentDO;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page03.dao
 * @Author: 高
 * @CreateTime: 2025-06-27 18:01
 * @Description: 用户数据访问接口，定义登录和注册方法
 * @Version: 1.0
 */
public interface IUserDAO {
    /**
     * 登录
     * @param account
     * @param password
     * @return
     */
    StudentDO login (String account , String password);

    Integer enroll(String account , String password , String name);
}

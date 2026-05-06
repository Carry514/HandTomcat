-- HandTomcat 示例数据库初始化脚本
-- 模块 03~05 的登录/注册功能依赖此表

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS handtomcat DEFAULT CHARACTER SET utf8mb4;

USE handtomcat;

DROP TABLE IF EXISTS student;
CREATE TABLE student (
                         id       INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
                         account  VARCHAR(50)  NOT NULL UNIQUE COMMENT '账号',
                         password VARCHAR(50)  NOT NULL COMMENT '密码',
                         name     VARCHAR(50)  DEFAULT NULL COMMENT '姓名',
                         gender   VARCHAR(10)  DEFAULT NULL COMMENT '性别'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 3. 插入测试数据
INSERT INTO student (account, password, name, gender) VALUES
                                                          ('admin',  '123456', '张三', '男'),
                                                          ('test',   '123456', '李四', '女');
-- 2. 创建学生表

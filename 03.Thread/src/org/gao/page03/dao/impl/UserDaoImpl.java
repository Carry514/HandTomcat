package org.gao.page03.dao.impl;

import org.gao.page03.dao.IUserDAO;
import org.gao.page03.entity.StudentDO;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * @BelongsProject: HandTomcat
 * @BelongsPackage: org.gao.page03.dao.impl
 * @Author: 高
 * @CreateTime: 2025-06-27 18:02
 * @Description: 用户数据访问层 JDBC 实现，原生 DriverManager + PreparedStatement
 * @Version: 1.0
 */
public class UserDaoImpl implements IUserDAO {

    private static String DB_DRIVER;
    private static String DB_URL;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;

    static {
        try (InputStream is = new FileInputStream("config/druid.properties")) {
            Properties props = new Properties();
            props.load(is);
            DB_DRIVER = props.getProperty("druid.driverClassName");
            DB_URL = props.getProperty("druid.url");
            DB_USERNAME = props.getProperty("druid.username");
            DB_PASSWORD = props.getProperty("druid.password");
            Class.forName(DB_DRIVER);
        } catch (Exception e) {
            throw new RuntimeException("加载数据库配置文件失败", e);
        }
    }

    @Override
    public StudentDO login(String account, String password) {

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "select * from student where account = ? and password = ?")) {

            pstmt.setString(1, account);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    StudentDO studentDO = new StudentDO();
                    studentDO.setName(rs.getString("name"));
                    return studentDO;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Integer enroll(String account, String password, String name) {

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(
                     "insert into student (account , password , name ) values (? , ? , ?)")) {

            pstmt.setString(1, account);
            pstmt.setString(2, password);
            pstmt.setString(3, name);

            int res = pstmt.executeUpdate();
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}

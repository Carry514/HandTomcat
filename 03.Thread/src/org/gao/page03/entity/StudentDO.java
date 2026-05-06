package org.gao.page03.entity;


/**
 * @BelongsProject: tomcat_maven
 * @BelongsPackage: org.gao.page01.entity
 * @Author: 高
 * @CreateTime: 2025-06-15 14:25
 * @Description: 学生实体类
 * @Version: 1.0
 */

public class StudentDO {
    private String name;
    private String gender;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

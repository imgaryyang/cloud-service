package com.excel.demo.anno;

import java.util.Date;

/**
 * 导出实体
 * @author mapp 2018/2/10.
 */
public class User {
 
    private String id;
    @ExceVo(name = "姓名",sort = 1)
    private String name;
    @ExceVo(name = "性别",sort = 2)
    private String sex;
    @ExceVo(name = "生日",sort = 3)
    private Date birthDay;
 
    public Date getBirthDay() {
        return birthDay;
    }
 
    public User(String id, String name, String sex, Date birthDay) {
        this.id=id;
        this.name=name;
        this.sex=sex;
        this.birthDay=birthDay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }
}

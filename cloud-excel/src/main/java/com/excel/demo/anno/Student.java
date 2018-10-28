package com.excel.demo.anno;

import java.util.Date;

/**
 * 导入实体
 * @author mapp 2018/2/10.
 */
public class Student {
 
    @ExceVo(sort = 1)
    private String name;
    @ExceVo(sort = 3)
    private String sex;
    @ExceVo(sort = 8, format = "yyyy-MM-dd")
    private Date birthDay;
    @ExceVo(sort = 4)
    private String className;
    @ExceVo(sort = 6)
    private String address;
    @ExceVo(sort = 5)
    private String phone;
    @ExceVo(sort = 7)
    private String love;
    @ExceVo(sort = 2)
    private int age;

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

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLove() {
        return love;
    }

    public void setLove(String love) {
        this.love = love;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

package com.excel.demo;

public class User {

    @Excel(name = "id", sort = 1, replace = {})
    private String id ;
    @Excel(name = "姓名", sort = 1, replace = {})
    private String name;

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

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

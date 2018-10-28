package com.excel.demo.anno;

import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试
 */
public class TestExport {
 
    public static void main(String[] args) throws ParseException {
        User user1 = new User("1", "盖伦", "男", DateUtils.parseDate("2018-2-11", new String[]{"yyyy-MM-dd"}));
        User user2 = new User("2", "德邦", "男", DateUtils.parseDate("2018-2-11", new String[]{"yyyy-MM-dd"}));
        User user3 = new User("3", "拉克丝", "女", DateUtils.parseDate("2018-2-11", new String[]{"yyyy-MM-dd"}));
        User user4 = new User("4", "寒冰", "女", DateUtils.parseDate("2018-2-11", new String[]{"yyyy-MM-dd"}));
 
        List<User> users = new ArrayList<User>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);
 
        Object[] objs = null;
        List<Object[]> list = new ArrayList<Object[]>();
 
        for (int i = 0; i < users.size(); i++) {
            objs = new Object[3];
            objs[0] = users.get(i).getId();
            objs[1] = users.get(i).getName();
            objs[2] = users.get(i).getSex();
            list.add(objs);
        }
 
        ExcelExportUtil<User> excelUtil = new ExcelExportUtil<User>(User.class);
        // 原始方式导出
//        excelUtil.export("test.xls", list, 2);
        // 基于注解导出
        excelUtil.export1("test.xls", users, 2);
    }
}
 
 
/**
 * 测试导入
 */
class TestReadExcel {
 
    public static void main(String[] args) throws ParseException {
        ExcelReadUtil<Student> excelReadUtil = new ExcelReadUtil<Student>(Student.class);
        List<Student> students = excelReadUtil.readExcel("D:\\student.xls", 2);
        for (Student student : students) {
            System.out.println(student);
        }
    }
}

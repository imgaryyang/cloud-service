package com.excel.demo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelTest {
    public static void exportExcelByXml (HttpServletResponse response, String fileName, Class<?> clazz, List<?> list) {
        // 注意这里要用xls，如果改为xlsx打开表格的时候会报错（我这边反正会这样）
        fileName += ".xls";
        String cntentType = "application/vnd.ms-excel";
        try {
            // 解决文件中文名乱码
            fileName = new String(fileName.getBytes("UTF-8"), "ISO_8859_1");
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("Content-Type", cntentType);
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            DataOutputStream rafs = new DataOutputStream(response.getOutputStream());
            // 创建表格数据xml
            StringBuffer data = createExcelXml(clazz, list, rafs);
            // 导出表格
            rafs.write(data.toString().getBytes());
            rafs.flush();
            rafs.close();
        } catch (UnsupportedEncodingException e) {
            // TODO: handle exception
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 每个sheet显示的数据行数 （因为是xls格式表格，我们将每个sheet设置为最多显示60000行数据）
    private static final int RECORDCOUNT = 60000;

    private static StringBuffer createExcelXml (Class<?> clazz, List<?> list, DataOutputStream rafs) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        // 拼接表头和样式  （这里我对XML的内容进行了部分的删减，和直接另存为的XML文件格式是不一样的，因为有一部分不要也没啥问题，所以就直接省略了）
        stringBuffer.append("<?xml version=\"1.0\"?><?mso-application progid=\"Excel.Sheet\"?>"
                + "<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\" "
                + "xmlns:o=\"urn:schemas-microsoft-com:office:office\" "
                + "xmlns:x=\"urn:schemas-microsoft-com:office:excel\" "
                + "xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\" "
                + "xmlns:html=\"http://www.w3.org/TR/REC-html40\">"
                + "<Styles><Style ss:ID=\"Default\" ss:Name=\"Normal\">"
                + "<Alignment ss:Vertical=\"Center\"/><Borders/>"
                + "<Font ss:FontName=\"宋体\" x:CharSet=\"134\" ss:Size=\"12\"/>"
                + "<Interior/><NumberFormat/><Protection/></Style></Styles>");
        // 获取列数和表头
        Map<String, Object> baseMap = getColumnAndTitle(clazz);
        int column = (int) baseMap.get("column");
        List<String> titleList = (List<String>) baseMap.get("titleList");
        // 标记当前循环了多少行数据
        int count = 0;
        // 数据总数
        int total = list.size();
        // sheet个数
        int sheetNo = total % RECORDCOUNT == 0 ? total / RECORDCOUNT : total / RECORDCOUNT + 1;
        if (total == 0) sheetNo = 1;
        for (int i = 0; i < sheetNo; i ++) {
            int index = 0;
            stringBuffer.append("<Worksheet ss:Name=\"sheet"+i+"\">");
            stringBuffer.append("<Table ss:ExpandedColumnCount=\"" + column
                    + "\" ss:ExpandedRowCount=\"" + RECORDCOUNT + 1
                    + "\" x:FullColumns=\"1\" x:FullRows=\"1\">");
            // 添加表头
            stringBuffer.append("<Row>");
            for (int t = 0; t < column; t ++) {
                stringBuffer.append("<Cell><Data ss:Type=\"String\">" + titleList.get(t) + "</Data></Cell>");
            }
            stringBuffer.append("</Row>");
            for (int j = count; j < total; j ++) {
                stringBuffer.append("<Row>");
                // 保存表格数据
                List<Object> dataList = new ArrayList<Object>();
                getProperty(list.get(j), clazz, dataList);
                // 此时循环遍历表格数据
                for (int d = 0; d < column; d ++) {
                    stringBuffer.append("<Cell><Data ss:Type=\"String\">" + dataList.get(d) +"</Data></Cell>");
                }
                stringBuffer.append("</Row>");
                index ++;
                count ++;
                if (index > RECORDCOUNT || index == RECORDCOUNT) break;
            }
            stringBuffer.append("</Table><WorksheetOptions xmlns=\"urn:schemas-microsoft-com:office:excel\">"
                    + "<FrozenNoSplit/><SplitHorizontal>1</SplitHorizontal>"
                    + "<ProtectObjects>False</ProtectObjects><ProtectScenarios>False</ProtectScenarios>"
                    + "</WorksheetOptions></Worksheet>");
            // 每一个工作簿输出一次，释放资源，防止内存溢出 （如果这里不加这个代码，拼接完成了数据量还是很大，导出的时候还是会报内存溢出的异常。你甚至可以将这个数值设置的更小一些）
            rafs.write(stringBuffer.toString().getBytes());
            rafs.flush();
            stringBuffer.setLength(0);
        }
        stringBuffer.append("</Workbook>");
        return stringBuffer;
    }

    private static Map<String, Object> getColumnAndTitle (Class<?> clazz) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 保存列数
        int column = 0;
        // 保存表头数据
        List<String> list = new ArrayList<String>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Excel.class)) {
                // 获取当前属性的注解值
                Annotation anno = field.getDeclaredAnnotation(Excel.class);
                // 转为Excel类
                Excel excel = (Excel) anno;
                // 列数+1
                column ++;
                // 保存表头
                list.add(excel.name());
            }
        }
        map.put("column", column);
        map.put("titleList", list);
        return map;
    }
    private static void getProperty (Object obj, Class<?> clazz, List<Object> dataList) {
        // 获取实体类中所有的属性
        Field [] fields = clazz.getDeclaredFields();
        // 遍历属性
        for (Field field : fields) {
            // 获取带有导出注解的属性
            if (field.isAnnotationPresent(Excel.class)) {
                // 创建一个字符数组，保存列名和值
                // 获取当前属性的注解值
                Annotation anno = field.getDeclaredAnnotation(Excel.class);
                // 转为Excel类
                Excel excel = (Excel) anno;
                // 获取当前属性
                String property = field.getName();
                // 拼接获取值方法
                String methodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
                try {
                    // 调用方法获取值
                    Method method = clazz.getDeclaredMethod(methodName);
                    // 获取当前值
                    Object data = method.invoke(obj);
                    // 如果此时是日期格式
                    String format = excel.exportFormat();
                    // 如果此时需要值的替换
                    String [] replace = excel.replace();
                    if (! StringUtils.isEmpty(format)) {
                        // 说明此时是日期格式，按照日期格式导出
                        if (! (data==null)) {
                            dataList.add(DateFormatUtils.format((Date) data,format));
                        } else {
                            dataList.add("");
                        }
                    } else if (replace.length > 0) {
                        // 此时需要值的替换
                        dataList.add(isReplace(replace, data + ""));
                    } else {
                        dataList.add(data == null? "" : data);
                    }
                } catch (NoSuchMethodException e) {
                    // TODO: handle exception
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    private static String isReplace (String [] replace, String id) {
        String result = id;
        for (int i = 0; i < replace.length; i ++) {
            String str = replace[i];
            // 拆解配置
            String [] strs = str.split("_");
            if (id.equals(strs[1])) {
                result = strs[0];
            }
        }
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException {
       // DataOutputStream outputStream = new DataOutputStream("D:/a.xls");
        Class<?> clazz = User.class;
        List<User> list = new ArrayList<>();
        list.add(new User("1","chenshan"));

        //StringBuffer data = createExcelXml(clazz, list, outputStream);
    }
}

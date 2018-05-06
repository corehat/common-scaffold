package org.corehat.common.scaffold;

import java.util.List;
import java.util.Map;

/**
 * 数据表字段工具类.
 * 
 * @since 1.0.0 
 * @date: 2016-8-25 上午1:25:04
 */
public class ColumnUtils {
    
    /**
     * MySql 数据库
     */
    public static final int DB_MYSQL = 1;
    
    
    public ColumnUtils() {
        
    }
    
    /**
     * 根据不同数据库获取对应Java类型.
     * 
     * @param column 数据表字段
     * @param dbType 数据库类型
     * @return Java类型
     * @throws Exception 类型转换异常
     * @date: 2016-8-25 上午1:26:14
     */
    public static String getColumnType(Map<String, Object> column, int dbType) throws Exception {
        String type = "";
        switch (dbType) {
            case DB_MYSQL:
                type = getColumnFromDB_Mysql(column);
                break;
            default:
                break;
        }
        return type;
    }
    
    /**
     * Integer 类型最大位数
     */
    private static final int INTEGER_MAX_LENGTH = 11;
    
    /**
     * Long 类型最大位数
     */
    private static final int LONG_MAX_LENGTH = 21;
    
    /**
     * 根据数据库字段类型获取对应Java类型.
     * 
     * @param column 数据表字段
     * @return Java类型
     * @throws Exception 类型转换异常
     * @date: 2016-8-25 上午1:33:03
     */
    private static String getColumnFromDB_Mysql(Map<String, Object> column) throws Exception {
        String type = String.valueOf(column.get("Type"));
        if (type.indexOf("varchar") > -1 || type.indexOf("text") > -1) {
            type = "String";
        } else if (type.indexOf("date") > -1 || type.indexOf("time") > -1) {
            type = "java.util.Date";
        } else if (type.indexOf("decimal") > -1 || type.indexOf("numeric") > -1) {
            type = "java.math.BigDecimal";
        } else if (type.indexOf("int") > -1) {
            int start = type.indexOf("(") + 1;
            int end = type.indexOf(")");
            int size = Integer.valueOf(String.valueOf(type.substring(start, end)));
            if (size < INTEGER_MAX_LENGTH) {
                type = "Integer";
            }  else if (size < LONG_MAX_LENGTH) {
                type = "Long";
            }  else {
                throw new Exception("\"" + column.get("Field") + "\"位数过大,程序可能出错,请不要超过18位");
            }
        } else if (type.indexOf("double") > -1 || type.indexOf("float") > -1) {
            type = "Double";
        } else {
            type = "String";
        }
        return type;
    }

    
    /**
     * 根据数据库字段类型设置对应Java类型.
     * 
     * @param columnList 数据表字段集合
     * @return 数据表字段集合包括对应Java类型
     * @throws Exception 类型转换异常
     * @date: 2016-8-25 上午1:40:15
     */
    public static List<Map<String, Object>> columnAddType(List<Map<String, Object>> columnList) throws Exception {
        for (int i = 0; i < columnList.size(); i++) {
            Map<String, Object> column = columnList.get(i);
            column.put("Jtype", getColumnType(column, DB_MYSQL));
        }
        return columnList;
    }

}

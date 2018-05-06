package org.corehat.common.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 配置文件工具类.
 * 
 * @author: lijx
 * @since 1.0.0 
 * @date: 2016-8-25 上午1:19:28
 */
public class PropertiesUtils {
    
    /**
     * 读取配置文件
     */
    private static ResourceBundle configBundle = ResourceBundle.getBundle("scaffold_cfg");
    
    /**
     * 设置配置文件路径
     * @param filePath 文件路径
     */
    public static void setPropertiesPath(String filePath) {
        configBundle = ResourceBundle.getBundle(filePath);
    }
    
    /**
     * 存放配置信息
     */
    private static Map<String, Object> config;
    
    /**
     * 通过key获取对应配置信息
     * @param key 配置属性key
     * @return 配置属性值
     * @date: 2016-8-25 上午1:20:48
     */
    public static String getValue(String key) {
        return configBundle.getString(key);
    }
    
    /**
     * 获取所有配置信息.
     * 
     * @return 配置信息
     * @date: 2016-8-25 上午1:21:25
     */
    public static Map<String, Object> getProopertyMap() {
        if (config == null) {
            //为空的时候同步
            synchronized (configBundle) {
                //同步之后再判断一次同步时是不是已经初始化了
                if (config == null) {
                    Map<String, Object> properties = new HashMap<String, Object>(); 
                    Enumeration<String> keys = configBundle.getKeys();
                    while (keys.hasMoreElements()) {
                        String key = keys.nextElement();
                        properties.put(key, String.valueOf(configBundle.getObject(key)).replaceAll(" ", ""));
                    }
                    config = properties;
                }
            }
        }
        return config;
    }
    
    /**
     * 获取所有配置信息.
     * 
     * @param filePath 文件路径
     * @return 配置信息
     * @date: 2016-8-25 上午1:21:25
     */
    public static Map<String, Object> getProopertyMap(String filePath) {
        ResourceBundle rb = ResourceBundle.getBundle(filePath);
        Map<String, Object> properties = new HashMap<String, Object>(); 
        Enumeration<String> keys = rb.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            properties.put(key, String.valueOf(rb.getObject(key)).replaceAll(" ", ""));
        }
        return properties;
    }
}

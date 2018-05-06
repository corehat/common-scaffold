package org.corehat.common.scaffold;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.corehat.common.util.PropertiesUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 代码生成脚手架.
 * 
 * @author: lijx
 * @since 1.0.0 
 * @date: 2016-8-25 上午1:41:23
 */
public class Scaffold {

    /**
     * 数据库连接池
     */
    private DriverManagerDataSource source;
    /**
     * 通用数据库操作类
     */
    private JdbcTemplate jdbcTemplate;

    /**
     * main方法.
     * 
     * @param args 调用参数
     * @author lijx
     * @since 1.0.0 
     * @date 2018-05-07 02:51
     */
    public static void main(String[] args) {
        Scaffold factory = new Scaffold();
        try {
            factory.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成后台文件
     */
    public void build() {
        // 获取配置信息
        Map<String, Object> root = PropertiesUtils.getProopertyMap();

        // 初始化数据库连接池
        source = new DriverManagerDataSource();
        // 设置jdbc信息
        source.setDriverClassName(PropertiesUtils.getValue("jdbc.driver"));
        source.setUrl(PropertiesUtils.getValue("jdbc.url"));
        source.setUsername(PropertiesUtils.getValue("jdbc.username"));
        source.setPassword(PropertiesUtils.getValue("jdbc.password"));

        // 创建通用数据库操作类
        jdbcTemplate = new JdbcTemplate(source);

        // 获取表相关信息
        String tableName = PropertiesUtils.getValue("tableName");
        String showColumnSql = " SHOW FULL COLUMNS FROM " + tableName;
        try {
            List<Map<String, Object>> columnList = jdbcTemplate.queryForList(showColumnSql);
            List<Map<String, Object>> list = ColumnUtils.columnAddType(columnList);
            root.put("columnList", list);
        } catch (Exception e) {
            System.out.println("数据库连接异常！");
            e.printStackTrace();
            return;
        }

        // 设置模板生成时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ahh:mm:ss");
        root.put("createDate", sdf.format(new Date()));

        // 自定义字段名称格式化函数
        root.put("nameFmt", new ColumnFormat());

        // 创建配置文件
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setDefaultEncoding("UTF-8");

        // 模板路径
        String templatePath = PropertiesUtils.getValue("templatePath");
        if (templatePath == null || "".equals(templatePath.trim())) {
            templatePath = "/template/scaffold";
        }
        // 获取模板文件列表
        List<String> templatePathL = new ArrayList<String>();
        // 1、判断是否为绝对路径
        try {
            File file = new File(templatePath);
            getTemplateFilesByDir(file, templatePathL);
            cfg.setDirectoryForTemplateLoading(file);
        } catch (Exception e1) {
            e1.printStackTrace();
            // 2、判断是否为相对路径
            try {
                File file = new File(this.getClass().getResource("/").getPath() + templatePath);
                getTemplateFilesByDir(file, templatePathL);
                cfg.setDirectoryForTemplateLoading(file);
            } catch (Exception e2) {
                e2.printStackTrace();
                // 3、或取jar模板文件列表
                try {
                    cfg.setClassForTemplateLoading(this.getClass(), templatePath);

                    // 处理jar包文件前面没有斜杠问题
                    if (templatePath.startsWith("/")) {
                        if (templatePath.length() > 1) {
                            templatePath = templatePath.substring(1);
                        } else {
                            templatePath = "";
                        }
                    }

                    getTemplateFilesByJar(templatePath, templatePathL);
                } catch (Exception e3) {
                    e3.printStackTrace();
                    System.out.println("读取模板文件失败！");
                    return;
                }
            }
        }
        if (templatePathL == null || templatePathL.isEmpty()) {
            System.out.println("没有找到模板文件！");
            return;
        }

        // 输出路径
        File output = new File(PropertiesUtils.getValue("outPath"));

        // 生成模板
        generateFile(templatePathL, templatePath, output, cfg, root);

    }

    /**
     * 根据目录获取模板文件列表
     * 
     * @param dir
     *            模板目录
     * @param templatePathL
     *            模板列表
     */
    private void getTemplateFilesByDir(File dir, List<String> templatePathL) {
        if (dir.isDirectory()) {
            File[] filelist = dir.listFiles();
            for (int i = 0; i < filelist.length; i++) {
                getTemplateFilesByDir(filelist[i], templatePathL);
            }
        } else if (dir.getName().indexOf(".ftl") > -1) {
            templatePathL.add(dir.getPath());
        }
    }

    /**
     * 获取jar模板文件列表
     * 
     * @param templatePath
     *            模板目录
     * @param templatePathL
     *            模板列表
     * @throws Exception
     */
    private void getTemplateFilesByJar(String templatePath, List<String> templatePathL) throws Exception {
        // 如果是jar获取的是jar包路径，如果不是jar文件获取的是目录
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        // 转换处理中文及空格
        path = java.net.URLDecoder.decode(path, "UTF-8");
        if (path.endsWith(".jar")) {
            // 读取jar包文件
            JarFile jarFile = new JarFile(path);
            Enumeration<JarEntry> em = jarFile.entries();
            while (em.hasMoreElements()) {
                JarEntry je = em.nextElement();
                System.out.println(je.getName());
                if (je.getName().startsWith(templatePath) && je.getName().endsWith(".ftl")) {
                    templatePathL.add(je.getName());
                }
            }
        } else {
            File file = new File(path);
            getTemplateFilesByDir(file, templatePathL);
        }
    }

    /**
     * 根据模板和数据生成代码
     * 
     * @param templatePathList 模板路径
     * @param templateDir 模板根目录
     * @param targetroot 输出路径
     * @param cfg 配置对象
     * @param root 数据来源
     * @date: 2016-8-25 上午1:51:17
     */
    private void generateFile(List<String> templatePathList, String templateDir, File targetroot, Configuration cfg,
            Map<String, Object> root) {
        for (String templatePath : templatePathList) {
            try {
                templatePath = templatePath.replaceAll("\\\\", "/");
                String relatePath = templatePath.substring(templatePath.indexOf(templateDir) + templateDir.length());
                int lastIndex = relatePath.lastIndexOf("/");
                String parentPath = lastIndex > 0 ? relatePath.substring(0, lastIndex) : "";

                File targetParent = new File((targetroot.getPath() + valueParam(parentPath, root)).toLowerCase());
                if (!targetParent.exists()) {
                    targetParent.mkdirs();
                }
                String tempfilename = relatePath;
                if (lastIndex > 0) {
                    tempfilename = tempfilename.substring(lastIndex + 1);
                }
                tempfilename = valueParam(tempfilename, root);

                File target = new File(
                        targetroot.getPath() + valueParam(parentPath, root) + "/" + tempfilename.replace(".ftl", ""));
                if (target.exists()) {
                    target.delete();
                }

                Template temp = cfg.getTemplate(relatePath);
                Writer out = new FileWriter(target);
                try {
                    temp.process(root, out);
                } catch (TemplateException e) {
                    e.printStackTrace();
                }
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将目录中的freemaker变量进行识别和替换
     * @param str 需要识别的目录
     * @param root 数据来源
     * @return 目录名称
     * @author lijx
     * @since 1.0.0 
     * @date 2018-05-07 02:56
     */
    private String valueParam(String str, Map<String, Object> root) {
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\$\\{[^\\$|\\{|\\}]*\\}");
        Matcher mather = pattern.matcher(str);
        while (mather.find()) {
            list.add(mather.group(0));
        }
        for (int i = 0; i < list.size(); i++) {
            String param = list.get(i);
            String key = param.replace("${", "").replace("}", "");
            str = str.replace(param, StringUtils.capitalize(String.valueOf(root.get(key))));
        }
        return str;
    }

}

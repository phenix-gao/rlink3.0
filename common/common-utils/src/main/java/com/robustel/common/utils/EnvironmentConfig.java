package com.robustel.common.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 系统环境配置
 * @author jingfangnan
 *
 */
public class EnvironmentConfig {

    /** The logger. */
    private static Logger logger = Logger.getLogger(EnvironmentConfig.class);

    /** The ec. */
    static EnvironmentConfig ec;// 创建对象ec

    /** The register. */
    private static Map<String, Properties> register = new HashMap<String, Properties>();// 静态对象初始化[在其它对象之前进行]

    /**
     * ReadConfig 构造子注解。.
     */
    private EnvironmentConfig() {
        super();
    }

    /**
     * 取得EnvironmentConfig的一个实例.
     * 
     * @return ec
     */
    public static EnvironmentConfig getInstance() {
        if (ec == null) {
            ec = new EnvironmentConfig();// 创建EnvironmentConfig对象
        }
        return ec;// 返回EnvironmentConfig对象
    }

    /**
     * get properties file instance
     * @param fileName
     * @return
     */

    private Properties getProperties(String fileName) {// 传递配置文件路径
        Properties p = register.get(fileName);// 将fileName存于一个HashTable
        if (p == null) {
            /**
             * 如果为空就尝试输入进文件
             */
            try {
                InputStream is = null;// 定义输入流is
                try {
                    is = new FileInputStream(fileName);// 创建输入流
                } catch (Exception e) {
                    if (fileName.startsWith("/"))
                        // 用getResourceAsStream()方法用于定位并打开外部文件。
                        is = EnvironmentConfig.class.getResourceAsStream(fileName);
                    else
                        is = EnvironmentConfig.class.getResourceAsStream("/" + fileName);
                }
                p = new Properties();
                p.load(new InputStreamReader(is, "UTF-8"));// 加载输入流
                register.put(fileName, p);// 将其存放于HashTable
                is.close();// 关闭输入流

            } catch (Exception e) {
                logger.error("getProperties: ", e);
            }
        }
        return p;// 返回Properties对象
    }

    /**
     * get the value from the key about properties
     * @param fileName
     * @param strKey
     * @return
     */

    public String getPropertyValue(String fileName, String strKey) {
        logger.info(String.format("正在读取配置文件%s", fileName));
        Properties p = getProperties(fileName);
        try {
            return p.getProperty(strKey);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return null;
    }

    /**
     * Write properties.
     * @param filePath
     * @param paraKey
     * @param paraValue
     */
    private void writeProperties(String filePath, String paraKey, String paraValue) {
        if (paraValue == null)
            paraValue = "";
        Properties props = getProperties(filePath);
        try {
            OutputStream ops = new FileOutputStream(filePath);
            props.setProperty(paraKey, paraValue);
            props.store(ops, "set");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write properties.
     * @param filePath
     * @param map
     */
    public synchronized void writeProperties(String filePath, Map<String, String> map) {
        Properties props = getProperties(filePath);
        try {
            OutputStream ops = new FileOutputStream(filePath);
            for (String key : map.keySet()) {
                String value = map.get(key);
                if (value == null)
                    value = "";
                props.setProperty(key, value);
            }
            props.store(ops, "set");

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 重新初始化
        initProperties(filePath);
    }

    /**
     * Inits the properties.
     * @param fileName
     */
    private void initProperties(String fileName) {
        logger.info("reload configuration file " + fileName);
        Properties p = null;
        try {
            InputStream is = null;// 定义输入流is
            try {
                is = new FileInputStream(fileName);// 创建输入流
            } catch (Exception e) {
                logger.warn("initProperties, error message: " + e.getLocalizedMessage());
                if (fileName.startsWith("/")) {
                    // 用getResourceAsStream()方法用于定位并打开外部文件。
                    is = EnvironmentConfig.class.getResourceAsStream(fileName);
                } else {
                    is = EnvironmentConfig.class.getResourceAsStream("/" + fileName);
                }
            }
            p = new Properties();
            p.load(is);// 加载输入流
            register.put(fileName, p);// 将其存放于HashMap
            is.close();// 关闭输入流
        } catch (Exception e) {
            logger.error("initProperties: ", e);
        }
    }

}

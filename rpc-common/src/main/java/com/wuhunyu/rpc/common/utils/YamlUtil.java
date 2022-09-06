package com.wuhunyu.rpc.common.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;

/**
 * yaml文件读取工具类
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 20:58
 */

@Slf4j
public class YamlUtil {

    private YamlUtil() {
    }

    /**
     * 读取指定配置文件
     *
     * @param configName 配置文件名称
     * @param clazz      model类
     * @param <T>        模型泛型
     * @return 模型对象
     * @throws IOException IOException
     */
    public static <T> T readConfig(String configName, Class<T> clazz) throws IOException {
        try (
                InputStream inputStream = clazz.getClassLoader()
                        .getResourceAsStream(configName)
        ) {
            // 配置文件不存在
            if (inputStream == null) {
                log.warn("{} 不存在", configName);
                return null;
            }
            Yaml yaml = new Yaml();
            return yaml.loadAs(inputStream, clazz);
        }
    }

    /**
     * 读取 配置字符串
     *
     * @param configStr 配置字符串
     * @param clazz     model类
     * @param <T>       模型泛型
     * @return 模型对象
     */
    public static <T> T readConfigStr(String configStr, Class<T> clazz) {
        if (StrUtil.isBlank(configStr)) {
            return null;
        }
        Yaml yaml = new Yaml();
        return yaml.loadAs(configStr, clazz);
    }

}

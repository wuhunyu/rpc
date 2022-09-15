package com.wuhunyu.rpc.server.utils;

import com.wuhunyu.rpc.server.annotation.RpcServer;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * bean 扫描工具类
 *
 * @author wuhunyu
 * @version 1.0
 * @date 2022-09-05 9:35
 */

public final class BeanScanUtil {

    private BeanScanUtil() {
    }

    public static Map<String, Class<?>> collectBeans(String packageName) {
        // bean存放容器
        Map<String, Class<?>> map = new ConcurrentHashMap<>();
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        ClassLoader classLoader = BeanScanUtil.class.getClassLoader();
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    File dir = new File(filePath);
                    List<File> fileList = new ArrayList<>();
                    BeanScanUtil.fetchFileList(dir, fileList);
                    for (File f : fileList) {
                        String fileName = f.getAbsolutePath();
                        if (fileName.endsWith(".class")) {
                            String noSuffixFileName = fileName.substring(8 + fileName.lastIndexOf("classes"), fileName.indexOf(".class"));
                            String filePackage = noSuffixFileName.replaceAll("\\\\", ".");
                            Class<?> clazz = classLoader.loadClass(filePackage);
                            BeanScanUtil.putBeans(clazz, map);
                        }
                    }
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        //jar:file:/D:/MyStudy/apidoc/target/apidoc-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!/com/demo/web
                        JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
                        jar = urlConnection.getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                // 如果是一个.class文件 而且不是目录
                                if (name.endsWith(".class") && !entry.isDirectory()
                                        && !name.endsWith("Instance.class")) {
                                    // 去掉后面的".class" 获取真正的类名
                                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                                    try {
                                        Class<?> clazz = classLoader.loadClass(packageName + '.' + className);
                                        BeanScanUtil.putBeans(clazz, map);
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 查找所有的文件 * * @param dir 路径 * @param fileList 文件集合
     */
    private static void fetchFileList(File dir, List<File> fileList) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    fetchFileList(f, fileList);
                }
            }
        } else {
            fileList.add(dir);
        }
    }

    /**
     * 收集 @RpcServer 标记的bean
     *
     * @param clazz 字节码
     */
    private static void putBeans(Class<?> clazz, Map<String, Class<?>> map) {
        RpcServer rpcServer = clazz.getAnnotation(RpcServer.class);
        if (null != rpcServer && rpcServer.enabled()) {
            Class<?>[] classes = rpcServer.value();
            if (classes.length > 0) {
                for (Class<?> aClass : classes) {
                    map.put(aClass.getName(), clazz);
                }
            } else {
                Class<?>[] interfaces = clazz.getInterfaces();
                if (interfaces.length > 0) {
                    for (Class<?> aClass : interfaces) {
                        map.put(aClass.getName(), clazz);
                    }
                }
            }
        }
    }

}

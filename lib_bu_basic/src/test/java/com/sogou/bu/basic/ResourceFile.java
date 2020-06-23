package com.sogou.bu.basic;

import java.io.File;
import java.net.URL;

/**
 * JVM testing purpose resource file in resources folder, which could
 * be open and read via ClassLoader
 * @author yangfeng
 */
public class ResourceFile {
    public static String getFilePath(Class<?> aClass, String name) {
        ClassLoader classLoader = aClass.getClassLoader();
        URL resource = classLoader.getResource(name);
        return resource.getPath();
    }

    public static File getFile(Class<?> aClass, String name) {
        return new File(getFilePath(aClass, name));
    }
}

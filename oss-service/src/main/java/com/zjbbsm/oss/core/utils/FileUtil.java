package com.zjbbsm.oss.core.utils;

import java.io.File;

/**
 * Author: GuanPeng
 * Date: 2016/11/11 16:46
 * <p/>
 * Description: application_context 工具类
 */
public class FileUtil {

    public static boolean exists(String file) {
        return new File(file).exists();
    }
}

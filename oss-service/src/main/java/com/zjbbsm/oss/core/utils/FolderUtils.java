package com.zjbbsm.oss.core.utils;

import com.zjbbsm.oss.core.common.Constant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Descripation: <br>
 * @Author:Peng.Guan <br>
 * @Date:2019/3/16 0016                          <br>
 */
public class FolderUtils {

    public static String getFolderPath(String fileName, String folderType){
        String preName = "";
        if (Constant.FOLDER_JIANHAO.equals(folderType)) {
            preName = Constant.FOLDER_JIANHAO;
        } else if (Constant.FOLDER_ORDER.equals(folderType)) {
            preName = Constant.FOLDER_ORDER;
        }else if (Constant.FOLDER_GOODS.equals(folderType)) {
            preName = Constant.FOLDER_GOODS;
        } else if (Constant.FOLDER_USER.equals(folderType)) {
            preName = Constant.FOLDER_USER;
        } else {

        }
        return preName + getCurrentTime() + fileName;
    }

    /**
     * 获得系统的当前时间
     */
    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date()) + File.separator;
    }

}

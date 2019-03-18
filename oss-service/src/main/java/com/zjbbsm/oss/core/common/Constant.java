package com.zjbbsm.oss.core.common;

import android.os.Environment;

import java.io.File;

/**
 * @Descripation: <br>
 * @Author:Peng.Guan <br>
 * @Date:2019/3/16 0016                          <br>
 */
public class Constant {

    private static final String FILE_DIR = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + File.separator + "oss/";

    //默认服务器上传回调地址
    public static String DEFAULT_CALLBACK_ADDRESS = "https://api.yiuxiu.com/UploadImage/UpImg";
    public static String DEFAULT_HOT_CALLBACK_ADDRESS = "https://api.yiuxiu.com/UploadImage/HotCicleUpImg";
    public static String DEFAULT_VIDEO_CALLBACK_ADDRESS = "https://api.yiuxiu.com/UploadImage/UploadVideo";

    //图片获取信息操作
    public static String OSS_PROCESS_IMAGE_INFO = "?x-oss-process=image/info";

    //图片信息
    public static String IMAGE_WIDTH = "ImageWidth";
    public static String IMAGE_HEIGHT = "ImageHeight";
    public static String OSS_CONFIG = "oss_config";

    //OSS阿里云配置信息
    public static String AccessKeyId = "AccessKeyId";
    public static String AccessKeySecret = "AccessKeySecret";
    public static String Endpoint = "Endpoint";
    public static String BucketAddress = "BucketAddress";
    public static String Bucket = "Bucket";

    //文件夹
    public static String FOLDER_GOODS = "goods/";
    public static String FOLDER_JIANHAO = "jianhao/";
    public static String FOLDER_ORDER = "order/";
    public static String FOLDER_USER = "user/";

}

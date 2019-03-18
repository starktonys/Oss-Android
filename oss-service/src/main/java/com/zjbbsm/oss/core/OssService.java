package com.zjbbsm.oss.core;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.OSSUtils;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.CreateBucketRequest;
import com.alibaba.sdk.android.oss.model.DeleteBucketRequest;
import com.alibaba.sdk.android.oss.model.DeleteBucketResult;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.HeadObjectRequest;
import com.alibaba.sdk.android.oss.model.HeadObjectResult;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.MultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zjbbsm.oss.core.common.Constant;
import com.zjbbsm.oss.core.listener.OSSCompletedCallback;
import com.zjbbsm.oss.core.utils.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Stack;

import okhttp3.Call;

/**
 * @Descripation: 阿里云Oss服务 <br>
 * @Author:Peng.Guan <br>
 * @Date:2019/3/11 <br>
 */
public class OssService {

    //OSS阿里云配置信息
    public static String AccessKeyId = "";
    public static String AccessKeySecret = "";
    public static String Endpoint = "";
    public static String BucketAddress = "";
    public static String Bucket = "";

    private static OSS mOSSClient;
    private static volatile OssService instance;
    private OSSCustomSignerCredentialProvider mCredentialProvider;
    private static Stack<OSSAsyncTask> mOSSAsyncTasks = new Stack<>();

    private OssService() {
    }

    public static OssService getInstance() {
        if (instance == null) {
            synchronized (OssService.class) {
                if (instance == null) {
                    return new OssService();
                }
            }
        }
        return instance;
    }

    //初始化使用参数
    public void init(Context context) {
        Utils.init((Application)context);
        if (!readFromLocal()) {
            mCredentialProvider = new OSSCustomSignerCredentialProvider() {
                @Override
                public String signContent(String content) {
                    return OSSUtils.sign(AccessKeyId, AccessKeySecret, content);
                }
            };
            mOSSClient = new OSSClient(context, Endpoint, mCredentialProvider);
        }
    }

    /**
     * 读取本地数据
     *
     * @return 有一个未空就返回true ,否则false
     */
    private boolean readFromLocal() {
        AccessKeyId = SPUtils.getInstance().getString(Constant.AccessKeyId);
        AccessKeySecret = SPUtils.getInstance().getString(Constant.AccessKeySecret);
        Endpoint = SPUtils.getInstance().getString(Constant.Endpoint);
        BucketAddress =  SPUtils.getInstance().getString(Constant.BucketAddress);
        Bucket = SPUtils.getInstance().getString(Constant.Bucket);

        if (TextUtils.isEmpty(AccessKeyId) ||
                TextUtils.isEmpty(AccessKeySecret) ||
                TextUtils.isEmpty(Endpoint) ||
                TextUtils.isEmpty(BucketAddress) ||
                TextUtils.isEmpty(Bucket)) {
            return true;
        }
        return false;
    }

    //初始化使用参数
    public void init(Context context, String accessKeyId, String accessKeySecret, String endpoint, String bucketAddress, String bucket) {
        writeToLocal(accessKeyId, accessKeySecret,  endpoint,  bucketAddress,  bucket);
        mCredentialProvider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                return OSSUtils.sign(AccessKeyId, AccessKeySecret, content);
            }
        };
        mOSSClient = new OSSClient(context, Endpoint, mCredentialProvider);
    }

    private void writeToLocal(String accessKeyId, String accessKeySecret, String endpoint, String bucketAddress, String bucket) {
        AccessKeyId = accessKeyId;
        AccessKeySecret = accessKeySecret;
        Endpoint = endpoint;
        BucketAddress = bucketAddress;
        Bucket = bucket;
        SPUtils.getInstance().put(Constant.AccessKeyId, accessKeyId);
        SPUtils.getInstance().put(Constant.AccessKeySecret, accessKeySecret);
        SPUtils.getInstance().put(Constant.Endpoint, endpoint);
        SPUtils.getInstance().put(Constant.BucketAddress, bucketAddress);
        SPUtils.getInstance().put(Constant.Bucket, bucket);
    }

    /**
     * 异步上传图片
     *
     * @param fileName             文件名称
     * @param localFile            本地文件位置
     * @param callbackParam        回调参数
     * @param ossCompletedCallback 回调监听
     */
    public void asyncUploadImage(String fileName, String localFile, HashMap<String, String> callbackParam, final OSSCompletedCallback ossCompletedCallback) {
        asyncUploadImage(fileName, localFile, Constant.DEFAULT_CALLBACK_ADDRESS, callbackParam, ossCompletedCallback);
    }

    /**
     * 异步上传图片
     *
     * @param fileName             文件名称
     * @param localFile            本地文件位置
     * @param callbackAddress      回调地址
     * @param callbackParam        回调参数
     * @param ossCompletedCallback 回调监听
     */
    public void asyncUploadImage(String fileName, String localFile, String callbackAddress, HashMap<String, String> callbackParam, final OSSCompletedCallback ossCompletedCallback) {
        if (fileName.equals("")) return;
        if (!FileUtil.exists(localFile)) return;
        // 构造上传请求

        //String fileName = Constant.FOLDER_USER + DateUtils.getCurrentTimeToStringEx() + file.getName();

        PutObjectRequest put = new PutObjectRequest(Bucket, fileName, localFile);
        put.setCRC64(OSSRequest.CRC64Config.YES);
        if (callbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(callbackParam);
        }

        // 异步上传时可以设置进度回调
        put.setProgressCallback((request, currentSize, totalSize) -> {
            if (ossCompletedCallback != null)
                ossCompletedCallback.onProgress(request, currentSize, totalSize);
        });

        OSSAsyncTask task = mOSSClient.asyncPutObject(put, new com.alibaba.sdk.android.oss.callback.OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onSuccess(request, result, null);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onFailure(request, clientExcepion, serviceException);
            }
        });
        mOSSAsyncTasks.push(task);
    }

    /**
     * 异步上传图片 附带图片地址和宽高信息
     *
     * @param fileName             文件名称
     * @param localFile            本地文件位置
     * @param callbackParam        回调参数
     * @param ossCompletedCallback 回调监听
     */
    public void asyncUploadImageWithWH(String fileName, String localFile, HashMap<String, String> callbackParam, final OSSCompletedCallback ossCompletedCallback) {
        asyncUploadImageWithWH(fileName, localFile, Constant.DEFAULT_HOT_CALLBACK_ADDRESS, callbackParam, ossCompletedCallback);
    }

    /**
     * 异步上传图片 附带图片地址和宽高信息
     *
     * @param fileName             文件名称
     * @param localFile            本地文件位置
     * @param callbackAddress      回调地址
     * @param callbackParam        回调参数
     * @param ossCompletedCallback 回调监听
     */
    public void asyncUploadImageWithWH(String fileName, String localFile, String callbackAddress, HashMap<String, String> callbackParam, final OSSCompletedCallback ossCompletedCallback) {
        if (fileName.equals("")) return;
        if (!FileUtil.exists(localFile)) return;
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(Bucket, fileName, localFile);
        put.setCRC64(OSSRequest.CRC64Config.YES);
        if (callbackAddress != null) {
            // 传入对应的上传回调参数，这里默认使用OSS提供的公共测试回调服务器地址
            put.setCallbackParam(callbackParam);
        }

        // 异步上传时可以设置进度回调
        put.setProgressCallback((request, currentSize, totalSize) -> {
            if (ossCompletedCallback != null)
                ossCompletedCallback.onProgress(request, currentSize, totalSize);
        });

        OSSAsyncTask task = mOSSClient.asyncPutObject(put, new com.alibaba.sdk.android.oss.callback.OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                String url = presignPublicObjectURL(Bucket, fileName);
                hashMap.put("url", url);
                OkHttpUtils
                        .get()
                        .url(url + Constant.OSS_PROCESS_IMAGE_INFO)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int i) {
                                if (ossCompletedCallback != null)
                                    ossCompletedCallback.onFailure(request, null, null);
                            }

                            @Override
                            public void onResponse(String data, int i) {
                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    hashMap.put(Constant.IMAGE_WIDTH, jsonObject.getJSONObject(Constant.IMAGE_WIDTH).getString("value"));
                                    hashMap.put(Constant.IMAGE_HEIGHT, jsonObject.getJSONObject(Constant.IMAGE_HEIGHT).getString("value"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    if (ossCompletedCallback != null)
                                        ossCompletedCallback.onSuccess(request, result, hashMap);
                                }
                            }
                        });
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onFailure(request, clientExcepion, serviceException);
            }
        });
        mOSSAsyncTasks.push(task);
    }

    /**
     * 异步断点续传视频资源
     *
     * @param fileName             文件名称
     * @param localFile            本地文件位置
     * @param callbackParam        回调参数
     * @param ossCompletedCallback 回调监听
     */
    public void asyncUploadVideo(String fileName, String localFile, HashMap<String, String> callbackParam, final OSSCompletedCallback ossCompletedCallback) {
        asyncUploadVideo(fileName, localFile, Constant.DEFAULT_VIDEO_CALLBACK_ADDRESS, callbackParam, ossCompletedCallback);
    }

    /**
     * 异步断点续传视频资源
     *
     * @param fileName             文件名称
     * @param localFile            本地文件位置
     * @param callbackAddress      回调地址
     * @param callbackParam        回调参数
     * @param ossCompletedCallback 回调监听
     */
    public void asyncUploadVideo(String fileName, String localFile, String callbackAddress, HashMap<String, String> callbackParam, final OSSCompletedCallback ossCompletedCallback) {
        //调用OSSAsyncTask cancel()方法时是否需要删除断点记录文件的设置
        String recordDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/youxiu/";
        File recordDir = new File(recordDirectory);
        // 要保证目录存在，如果不存在则主动创建
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }

        // 创建断点上传请求，参数中给出断点记录文件的保存位置，需是一个文件夹的绝对路径
        ResumableUploadRequest request = new ResumableUploadRequest(Bucket, fileName, localFile, recordDirectory);
        //设置false,取消时，不删除断点记录文件，如果不进行设置，默认true，是会删除断点记录文件，下次再进行上传时会重新上传。
        request.setDeleteUploadOnCancelling(false);
        if (callbackAddress != null) {
            request.setCallbackParam(callbackParam);
        }
        // 设置上传过程回调
        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
            @Override
            public void onProgress(ResumableUploadRequest request, long currentSize, long totalSize) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onProgress(request, currentSize, totalSize);
            }
        });

        OSSAsyncTask task = mOSSClient.asyncResumableUpload(request, new com.alibaba.sdk.android.oss.callback.OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onSuccess(request, result, null);
            }

            @Override
            public void onFailure(ResumableUploadRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onFailure(request, clientExcepion, serviceException);
            }
        });
        mOSSAsyncTasks.push(task);
    }

    /**
     * 解析共有资源的图片路径
     *
     * @param bucket   存储空间
     * @param fileName 文件名称
     * @return 外公的公共访问路径
     */
    public static String presignPublicObjectURL(String bucket, String fileName) {
        return mOSSClient.presignPublicObjectURL(bucket, fileName);
    }

    /**
     * 异步加载图片
     *
     * @param fileName             文件名称
     * @param ossCompletedCallback 上传回调
     */
    public void asyncLoadImage(String fileName, OSSCompletedCallback ossCompletedCallback) {
        if ((fileName == null) || fileName.equals("")) return;
        GetObjectRequest get = new GetObjectRequest(Bucket, fileName);
        get.setCRC64(OSSRequest.CRC64Config.YES);
        get.setProgressListener((request, currentSize, totalSize) -> {
            if (ossCompletedCallback != null)
                ossCompletedCallback.onProgress(request, currentSize, totalSize);
        });
        OSSAsyncTask task = mOSSClient.asyncGetObject(get, new com.alibaba.sdk.android.oss.callback.OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onSuccess(request, result, null);
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onFailure(request, clientExcepion, serviceException);
            }
        });
        mOSSAsyncTasks.push(task);
    }

    /**
     * 异步上传Multipart资源
     *
     * @param uploadKey
     * @param uploadFilePath
     * @param ossCompletedCallback
     */
    public void asyncMultipartUpload(String uploadKey, String uploadFilePath, OSSCompletedCallback ossCompletedCallback) {
        MultipartUploadRequest request = new MultipartUploadRequest(Bucket, uploadKey,
                uploadFilePath);
        request.setCRC64(OSSRequest.CRC64Config.YES);
        request.setProgressCallback(new OSSProgressCallback<MultipartUploadRequest>() {
            @Override
            public void onProgress(MultipartUploadRequest request, long currentSize, long totalSize) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onProgress(request, currentSize, totalSize);
            }
        });

        OSSAsyncTask task = mOSSClient.asyncMultipartUpload(request, new com.alibaba.sdk.android.oss.callback.OSSCompletedCallback<MultipartUploadRequest, CompleteMultipartUploadResult>() {
            @Override
            public void onSuccess(MultipartUploadRequest request, CompleteMultipartUploadResult result) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onSuccess(request, result, null);
            }

            @Override
            public void onFailure(MultipartUploadRequest request, ClientException clientException, ServiceException serviceException) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onFailure(request, clientException, serviceException);
            }
        });
        mOSSAsyncTasks.push(task);
    }

    /**
     *  扫描根目录的所有文件
     *
     * @param prefixName 文件前缀
     * @param ossCompletedCallback 回调
     */
    public void asyncListObjectsWithBucketName(String prefixName, OSSCompletedCallback ossCompletedCallback) {
        ListObjectsRequest listObjects = new ListObjectsRequest(Bucket);
        // Sets the prefix
        listObjects.setPrefix(prefixName);
        listObjects.setDelimiter("/");
        OSSAsyncTask task = mOSSClient.asyncListObjects(listObjects, new com.alibaba.sdk.android.oss.callback.OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onSuccess(request, result, null);
            }

            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onFailure(request, clientExcepion, serviceException);
            }
        });
        mOSSAsyncTasks.push(task);
    }

    // Gets file's metadata
    public void headObject(String objectKey, OSSCompletedCallback ossCompletedCallback) {
        // Creates a request to get the file's metadata
        HeadObjectRequest head = new HeadObjectRequest(Bucket, objectKey);
        OSSAsyncTask task = mOSSClient.asyncHeadObject(head, new com.alibaba.sdk.android.oss.callback.OSSCompletedCallback<HeadObjectRequest, HeadObjectResult>() {
            @Override
            public void onSuccess(HeadObjectRequest request, HeadObjectResult result) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onSuccess(request, result, null);
            }

            @Override
            public void onFailure(HeadObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onFailure(request, clientExcepion, serviceException);
            }
        });
    }

    public void createBucket(final String bucket) {
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucket);
        // 创建bucket
        try {
            mOSSClient.createBucket(createBucketRequest);
        } catch (ClientException clientException) {
            clientException.printStackTrace();
        } catch (ServiceException serviceException) {
            serviceException.printStackTrace();
        }
    }


    /**
     * Delete a non-empty bucket.
     * Create a bucket, and add files into it.
     * Try to delete the bucket and failure is expected.
     * Then delete file and then delete bucket
     */
    public void deleteNotEmptyBucket(final String bucket, String resumeObjectKey, final String filePath, OSSCompletedCallback ossCompletedCallback) {
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucket);
        // 创建bucket
        try {
            mOSSClient.createBucket(createBucketRequest);
        } catch (ClientException clientException) {
            clientException.printStackTrace();
        } catch (ServiceException serviceException) {
            serviceException.printStackTrace();
        }

        PutObjectRequest putObjectRequest = new PutObjectRequest(Bucket,  resumeObjectKey, filePath);
        try {
            mOSSClient.putObject(putObjectRequest);
        } catch (ClientException clientException) {
            clientException.printStackTrace();
        } catch (ServiceException serviceException) {
            serviceException.printStackTrace();
        }
        final DeleteBucketRequest deleteBucketRequest = new DeleteBucketRequest(bucket);
        OSSAsyncTask task = mOSSClient.asyncDeleteBucket(deleteBucketRequest, new com.alibaba.sdk.android.oss.callback.OSSCompletedCallback<DeleteBucketRequest, DeleteBucketResult>() {
            @Override
            public void onSuccess(DeleteBucketRequest request, DeleteBucketResult result) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onSuccess(request, result, null);
            }

            @Override
            public void onFailure(DeleteBucketRequest request, ClientException clientException, ServiceException serviceException) {
                if (ossCompletedCallback != null)
                    ossCompletedCallback.onFailure(request, clientException, serviceException);
            }
        });
    }

    /**
     * 取消所有任务队列
     */
    public void cancelAll() {
        for (OSSAsyncTask task : mOSSAsyncTasks) {
            if (task != null) task.cancel();
        }
    }

}

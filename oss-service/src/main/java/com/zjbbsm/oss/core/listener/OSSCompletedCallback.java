package com.zjbbsm.oss.core.listener;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.OSSResult;

import java.util.Map;

/**
 * @Descripation: 阿里云Oss服务回调 <br>
 * @Author:Peng.Guan <br>
 * @Date:2019/3/11 <br>
 */
public interface OSSCompletedCallback<T1 extends OSSRequest, T2 extends OSSResult> {

    public void onSuccess(T1 request, T2 result, Map extraData);

    public void onFailure(T1 request, ClientException clientException, ServiceException serviceException);

    public void onProgress(T1 request, long currentSize, long totalSize);
}

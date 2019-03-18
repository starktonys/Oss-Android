package tony.stark.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.OSSResult;
import com.zjbbsm.oss.core.OssService;
import com.zjbbsm.oss.core.common.Constant;
import com.zjbbsm.oss.core.listener.OSSCompletedCallback;
import com.zjbbsm.oss.core.utils.FolderUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import me.iwf.photopicker.PhotoPicker;
import tony.stark.me.R;
import tony.stark.me.utils.HashMapUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // TODO: 2019/3/18 0018 login app
    public void doLogin() {
        //用户登录app初始化 oss 配置信息
        OssService.getInstance().init(getApplication(), "", "", "", "", "");
    }


    public void uploadVideo(View view) {
        File file = new File("");
        OssService.getInstance().asyncUploadVideo(FolderUtils.getFolderPath(file.getName(),Constant.FOLDER_USER), file.toString(), HashMapUtil.initUploadImageParams(""), new OSSCompletedCallback() {
            @Override
            public void onSuccess(OSSRequest request, OSSResult result, Map extraData) {

            }

            @Override
            public void onFailure(OSSRequest request, ClientException clientException, ServiceException serviceException) {

            }

            @Override
            public void onProgress(OSSRequest request, long currentSize, long totalSize) {

            }
        });
    }

    public void chooseImage(View view) {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> photos =
                        data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                uploadImage(photos.get(0));
            }
        }
    }

    private void uploadImage(String path) {
        File file = new File(path);
        OssService.getInstance().asyncUploadImageWithWH(FolderUtils.getFolderPath(file.getName(), Constant.FOLDER_USER), file.toString(), HashMapUtil.initUploadImageParams("type"), new OSSCompletedCallback() {
            @Override
            public void onSuccess(OSSRequest request, OSSResult result, Map extraData) {

            }

            @Override
            public void onFailure(OSSRequest request, ClientException clientException, ServiceException serviceException) {

            }

            @Override
            public void onProgress(OSSRequest request, long currentSize, long totalSize) {

            }
        });
    }

}

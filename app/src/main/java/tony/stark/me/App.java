package tony.stark.me;

import android.app.Application;

import com.zjbbsm.oss.core.OssService;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        OssService.getInstance().init(this);
    }
}

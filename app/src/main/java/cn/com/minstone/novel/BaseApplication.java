package cn.com.minstone.novel;

import android.app.Application;
import android.support.multidex.MultiDex;

import cn.bmob.v3.Bmob;
import cn.com.minstone.novel.util.CrashLogUtil;
import me.yokeyword.fragmentation.Fragmentation;

/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/1/29
 * @author king
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fragmentation.builder()
                .debug(BuildConfig.DEBUG)
                .install();

        Bmob.initialize(this, "8da43f27f56bc2571f806f2b937a52a5");

        CrashLogUtil handler = CrashLogUtil.getInstance();
        handler.init(getApplicationContext());
        MultiDex.install(this);
    }
}

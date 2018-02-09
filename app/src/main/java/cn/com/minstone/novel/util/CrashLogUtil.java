package cn.com.minstone.novel.util;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;

/***
 * 自定义崩溃处理日志类，用于捕获崩溃异常
 *
 * @since 2018/2/9
 * @author king
 */

public class CrashLogUtil implements Thread.UncaughtExceptionHandler {
    public static final String TAG = CrashLogUtil.class.getSimpleName();
    private static CrashLogUtil handlerInstance = new CrashLogUtil();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mHandler;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private CrashLogUtil() {
    }

    public static CrashLogUtil getInstance() {
        return handlerInstance;
    }

    public void init(Context ctx) {
        mContext = ctx;
        mHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mHandler != null) {
            mHandler.uncaughtException(thread, ex);
        } else {
            try {
                //如果处理了错误，则保持程序持续运行2秒，以保存日志。
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //异常退出
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:处理了该异常信息;否则返回false
     */
    public boolean handleException(Throwable ex) {
        if (ex == null) {

            return false;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "出错了", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        Log.e("crash", ex.getMessage(), ex);

        return true;
    }

    /**
     * 保存崩溃日志
     *
     * @param tr
     */
//    public void savaLog(Throwable tr) {
//        try {
//            File errorFile = FileUtil.getFile(FileUtil.getCacheDir(mContext) + "/log/", "crash.log");
//            Log.i(errorFile.getAbsolutePath());
//            if (!errorFile.exists()) {
//                errorFile.createNewFile();
//            }
//            OutputStream os = new FileOutputStream(errorFile, true);
//            os.write(("-----错误日志：" + dateFormat.format(new Date()) + "------\n\n").getBytes("utf-8"));
//            PrintStream ps = new PrintStream(os);
//            tr.printStackTrace(ps);
//            ps.flush();
//            os.flush();
//            ps.close();
//            os.close();
//        } catch (Exception e) {
//            Log.e(e);
//        }
//    }
}

package cn.com.minstone.novel.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;


/***
 * 名称：<br>
 * 描述：
 * 最近修改时间：
 * @since 2018/1/9
 * @author king
 */

public class ImageUtil {

    /**
     * 展示图片
     *
     * @param context
     * @param uri
     * @param imageView
     */
    public static void showImage(Context context, String uri, ImageView imageView) {

        RequestOptions options = new RequestOptions();
        options.fitCenter()
                .fitCenter()
                .priority(Priority.IMMEDIATE);
        if (uri.endsWith(".gif")) { //显示gif图
            Glide.with(context).asGif().load(uri)
                    .apply(options)
                    .into(imageView);
        } else {
            Glide.with(context).load(uri)
                    .apply(options)
                    .into(imageView);
        }

    }

    public static boolean isGif(String path) {

        if (TextUtils.isEmpty(path)) {
            return false;
        }
        if (path.endsWith(".gif")) {
            return true;
        }
        return false;
    }

    /**
     * 保存图片到指定文件夹
     *
     * @param bmp
     * @param filePath
     * @return
     */
    public static boolean saveBitmapToFile(Bitmap bmp, String filePath) {
        if (bmp == null || filePath == null)
            return false;
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;

        try {
            stream = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bmp.compress(format, quality, stream);
    }

}

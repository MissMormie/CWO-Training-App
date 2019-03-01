package nl.multimedia_engineer.cwo_app.tasks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nl.multimedia_engineer.cwo_app.util.PhotoCompressionUtil;

/**
 * Created by root on 9/14/17.
 */

public class ImageCompressTask implements Runnable {

    public interface IImageCompressTaskListener {
        public void onComplete(Map<Size, File> compressed);
        public void onError(Throwable error);
    }

    private Context mContext;
    private String path;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Map<Size, File> resultMap = new HashMap();
    private IImageCompressTaskListener mIImageCompressTaskListener;
    private Size[] sizes;

    public enum Size {
        LARGE(300, 300), NORMAL(150, 150), THUMBNAIL(75,75);

        final int width;
        final int height;
        Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }


    public ImageCompressTask(Context context, String path, IImageCompressTaskListener compressTaskListener, Size[] sizes) {
        this.sizes = sizes;
        this.path = path;
        mContext = context;

        mIImageCompressTaskListener = compressTaskListener;
    }

    @Override
    public void run() {
    Log.wtf("Runnable", "In Runnable");
        try {

            //Loop through all the given paths and collect the compressed file from Util.getCompressed(Context, String)
            for (Size size: sizes) {
                Log.wtf("Runnable", "adding size: " + size.toString());

                File file = PhotoCompressionUtil.getCompressed(mContext, path, size.width, size.height);
                //add it!
                resultMap.put(size, file);

            }
            //use Handler to post the result back to the main Thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("Runnable", "CallBack");
                    if(mIImageCompressTaskListener != null)
                        mIImageCompressTaskListener.onComplete(resultMap);
                }
            });
        }catch (final IOException ex) {
            //There was an error, report the error back through the callback
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mIImageCompressTaskListener != null)
                        mIImageCompressTaskListener.onError(ex);
                }
            });
        }
    }
}
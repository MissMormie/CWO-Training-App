package nl.multimedia_engineer.watersport_training.tasks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nl.multimedia_engineer.watersport_training.util.PhotoCompressionUtil;

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
        try {

            //Loop through all the given paths and collect the compressed file from Util.getCompressed(Context, String)
            for (Size size: sizes) {

                File file = PhotoCompressionUtil.getCompressed(mContext, path, size.width, size.height);
                //add it!
                resultMap.put(size, file);

            }
            //use Handler to post the result back to the main Thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
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
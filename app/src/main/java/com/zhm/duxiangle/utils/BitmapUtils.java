package com.zhm.duxiangle.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;

import java.io.ByteArrayOutputStream;

/**
 * Created by zhuanghm(183340093@qq.com) on 2015/10/9.
 */
public class BitmapUtils {

    static BitmapUtils skBitmapUtils;
    private static Context mContext;
    private com.lidroid.xutils.BitmapUtils bitmapUtils;

    private BitmapUtils() {
    }

    public com.lidroid.xutils.BitmapUtils getXUtilsBitmap() {
        return bitmapUtils == null ? new com.lidroid.xutils.BitmapUtils(mContext) : bitmapUtils;
    }

    public static BitmapUtils getInstance(Context context) {
        if (null == context) {
            return null;
        }
        if (null == skBitmapUtils) {
            skBitmapUtils = new BitmapUtils();
        }
        mContext = context;
        return skBitmapUtils;
    }

    /**
     * 重绘图片，设置倒影
     *
     * @param originalImage
     * @return
     */
    public static Bitmap createReflectedImage(Bitmap originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        Matrix matrix = new Matrix();
        // 实现图片翻转90度
        matrix.preScale(1, -1);
        // 创建倒影图片（是原始图片的一半大小）
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 3, width, height / 3, matrix, false);
        // 创建总图片（原图片 + 倒影图片）
        Bitmap finalReflection = Bitmap.createBitmap(width, (height + height / 3), Bitmap.Config.ARGB_8888);
        // 创建画布
        Canvas canvas = new Canvas(finalReflection);
        canvas.drawBitmap(originalImage, 0, 0, null);
        //把倒影图片画到画布上
        canvas.drawBitmap(reflectionImage, 0, height + 1, null);
        Paint shaderPaint = new Paint();
        //创建线性渐变LinearGradient对象
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, finalReflection.getHeight() + 1, 0x70ffffff,
                0x00ffffff, Shader.TileMode.MIRROR);
        shaderPaint.setShader(shader);
        shaderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果。
        canvas.drawRect(0, height + 1, width, finalReflection.getHeight(), shaderPaint);
        return finalReflection;
    }

    /**
     * 根据url异步加载图片
     *
     * @param container
     * @param url
     * @return
     */
    public Bitmap setAvatar(ImageView container, String url, final Toolbar toolbar) {
        if (null == bitmapUtils) {
            bitmapUtils = new com.lidroid.xutils.BitmapUtils(mContext);
        }

        bitmapUtils.display(container, url, new BitmapLoadCallBack<ImageView>() {
            @Override
            public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
                container.setImageBitmap(createReflectedImage(bitmap));
//                toolbar.setLogo(container.getDrawable());
            }

            @Override
            public void onLoadFailed(ImageView container, String uri, Drawable drawable) {

            }
        });
        return null;
    }

    public Bitmap setAvatar(ImageView container, String url) {
        if (null == bitmapUtils) {
            bitmapUtils = new com.lidroid.xutils.BitmapUtils(mContext);
        }

        bitmapUtils.display(container, url, new BitmapLoadCallBack<ImageView>() {
            @Override
            public void onLoadCompleted(ImageView container, String uri, Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
                container.setImageBitmap(createReflectedImage(bitmap));
            }

            @Override
            public void onLoadFailed(ImageView container, String uri, Drawable drawable) {

            }
        });
        return null;
    }

    public boolean cleanCache() {
        bitmapUtils.clearCache();
        return true;
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}

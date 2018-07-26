package com.nemestats.boardgametracker.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

/**
 * Created by geomehedeniuc on 3/17/18.
 */

public class PicassoRoundedCornerTransformation implements Transformation {

    private float mCornerRadius = 10f;

    public PicassoRoundedCornerTransformation() {
    }

    public PicassoRoundedCornerTransformation(float cornerRadius) {
        mCornerRadius = cornerRadius;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float cornerRadius = size / mCornerRadius;
        canvas.drawRoundRect(new RectF(0, 0, size, size), cornerRadius, cornerRadius, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "rounded";
    }

}

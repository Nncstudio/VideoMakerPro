package com.render.video.segment;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.render.video.opengl.BitmapTexture;
import com.render.video.opengl.GLESCanvas;
import com.render.video.util.ScaleType;

/**
 * Created by huangwei on 2018/10/26.
 */
public class WaterMarkSegment extends SingleBitmapSegment implements Cloneable{

    private Bitmap mBitmap;
    private RectF mDstRect;
    private float mAlpha;

    public void setWaterMark(Bitmap bitmap, RectF dstRect,float alpha) {
        mBitmap = bitmap;
        mDstRect = new RectF(dstRect);
        mAlpha = alpha;
        synchronized (this) {
            mBitmapInfo = null;
        }
    }

    @Override
    public void onPrepare() {
        onDataPrepared();
    }

    @Override
    public void drawFrame(GLESCanvas canvas, float segmentRate) {
        synchronized (this) {
            if (mBitmapInfo == null && mBitmap != null && mDstRect != null) {
                BitmapTexture bitmapTexture = new BitmapTexture(mBitmap);
                bitmapTexture.setOpaque(false);
                mBitmapInfo = new BitmapInfo();
                mBitmapInfo.srcRect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
                mBitmapInfo.srcShowRect.set(mBitmapInfo.srcRect);
                mBitmapInfo.scaleType = ScaleType.FIT_XY;
                mBitmapInfo.bitmapTexture = bitmapTexture;
                onDataPrepared();
            }
        }
        if (!mDataPrepared) {
            return;
        }
        if (mBitmapInfo != null && mBitmapInfo.makeTextureAvailable(canvas)) {
            canvas.save();
            canvas.setAlpha(mAlpha);
            canvas.drawTexture(mBitmapInfo.bitmapTexture, mBitmapInfo.srcShowRect, mDstRect);
            canvas.restore();
        }
    }

    @Override
    public WaterMarkSegment clone(){
        WaterMarkSegment waterMarkSegment = new WaterMarkSegment();
        waterMarkSegment.setWaterMark(mBitmap,mDstRect,mAlpha);
        return waterMarkSegment;
    }
}

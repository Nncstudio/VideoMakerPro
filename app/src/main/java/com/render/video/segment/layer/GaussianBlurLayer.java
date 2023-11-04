package com.render.video.segment.layer;

import android.graphics.Bitmap;
import com.render.video.opengl.BitmapTexture;
import com.render.video.opengl.GLESCanvas;
import com.render.video.segment.BitmapInfo;
import com.render.video.util.AppResources;
import com.render.video.util.stackblur.StackBlurManager;

import java.util.List;

/**
 * Created by huangwei on 2015/6/10.
 */
public class GaussianBlurLayer extends MovieLayer {
    public static final int BLUR_RADIUS_DEFAULT = 10;
    private BitmapInfo mBitmapInfo;
    private BitmapInfo mBluredBitmapInfo;

    private float mBlurRadius = 10;

    @Override
    public void drawFrame(GLESCanvas canvas, float progress) {
        if (mBitmapInfo != null && mBluredBitmapInfo != null) {
            canvas.drawTexture(mBitmapInfo.bitmapTexture, mBitmapInfo.srcShowRect, mViewprotRect);
            canvas.drawMixed(mBluredBitmapInfo.bitmapTexture, 0, 1 - progress, mBluredBitmapInfo.srcShowRect, mViewprotRect);
        }
    }

    @Override
    public int getRequiredPhotoNum() {
        return 1;
    }

    @Override
    public void prepare() {

    }

    @Override
    public void release() {

    }

    @Override
    public void allocPhotos(List<BitmapInfo> bitmapInfos) {
        super.allocPhotos(bitmapInfos);
        if (bitmapInfos != null && bitmapInfos.size() > 0) {
            mBitmapInfo = bitmapInfos.get(0);
        }

        if (mBitmapInfo != null) {
            mBlurRadius = BLUR_RADIUS_DEFAULT * AppResources.getInstance().getAppDensity() + 0.5f;
            Bitmap bitmap = mBitmapInfo.bitmapTexture.getBitmap();
            StackBlurManager manager = new StackBlurManager(bitmap, 0.125f);
            Bitmap bluredBitmap = manager.process((int) (mBlurRadius * 0.125f));
            mBluredBitmapInfo = new BitmapInfo();
            mBluredBitmapInfo.bitmapTexture = new BitmapTexture(bluredBitmap);
            mBluredBitmapInfo.bitmapTexture.setOpaque(false);
            mBluredBitmapInfo.srcRect.set(0, 0, bluredBitmap.getWidth(), bluredBitmap.getHeight());
            mBluredBitmapInfo.applyScaleType(mViewprotRect);
        }
    }

    @Override
    public void setViewprot(int l, int t, int r, int b) {
        super.setViewprot(l, t, r, b);
        if (mBluredBitmapInfo != null) {
            mBluredBitmapInfo.applyScaleType(mViewprotRect);
        }
    }
}

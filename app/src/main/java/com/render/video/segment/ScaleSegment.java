package com.render.video.segment;

import android.graphics.Bitmap;
import com.render.video.model.PhotoData;
import com.render.video.opengl.BitmapTexture;
import com.render.video.opengl.GLESCanvas;
import com.render.video.segment.animation.SrcAnimation;
import com.render.video.segment.animation.SrcScaleAnimation;
import com.render.video.util.Utils;

/**
 * Created by huangwei on 2015/5/30.
 */
public class ScaleSegment extends SingleBitmapSegment {

    private SrcAnimation mSrcAnimation;
    private float mFrom = 1f;
    private float mTo = 1f;

    public ScaleSegment(int duration, float from, float to) {
        this.mDuration = duration;
        mFrom = from;
        mTo = to;
    }

    public void onPrepare() {
        PhotoData photoData = this.getPhoto(0);
        if (photoData != null) {
            photoData.prepareData(4, new PluginListener(this));
        } else {
            throw new NullPointerException("PhotoData is null");
        }
    }

    protected void onDataPrepared() {
        mBitmapInfo.applyScaleType(mViewportRect);
        mSrcAnimation = new SrcScaleAnimation(mBitmapInfo.srcRect, mBitmapInfo.srcShowRect, mViewportRect, mFrom, mTo);
        mDataPrepared = true;
    }

    public void drawFrame(float segmentProgress) {
    }

    public void drawFrame(GLESCanvas canvas, float segmentRate) {
        if (!mDataPrepared) {
            return;
        }
        mSrcAnimation.update(segmentRate);
        if (this.mBitmapInfo != null && mBitmapInfo.bitmapTexture != null) {
            canvas.drawTexture(this.mBitmapInfo.bitmapTexture, this.mBitmapInfo.srcShowRect, this.mViewportRect);
        }

    }

    public int getRequiredPhotoNum() {
        return 1;
    }

    private class PluginListener extends PhotoData.SimpleOnDataLoadListener {
        private ScaleSegment segment;

        public PluginListener(ScaleSegment segment) {
            this.segment = segment;
        }

        @Override
        public void onDataLoaded(PhotoData photoData, Bitmap bitmap) {
            boolean success = false;
            if (Utils.isBitmapAvailable(bitmap)) {
                segment.mBitmapInfo = new BitmapInfo();
                segment.mBitmapInfo.bitmapTexture = new BitmapTexture(bitmap);
                segment.mBitmapInfo.srcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
                segment.mBitmapInfo.srcShowRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
                segment.onDataPrepared();
                success = true;
            }

            if (segment.mOnSegmentPrepareListener != null) {
                segment.mOnSegmentPrepareListener.onSegmentPrepared(success);
            }
        }
    }
}

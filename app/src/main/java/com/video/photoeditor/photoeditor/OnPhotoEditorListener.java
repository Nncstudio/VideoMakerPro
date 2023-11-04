package com.video.photoeditor.photoeditor;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.video.photoeditor.view.RoundFrameLayout;
import com.video.photoeditor.view.StrokeTextView;

public interface OnPhotoEditorListener {
    void onAddViewListener(ViewType viewType, int i);

    void onAdded(StrokeTextView strokeTextView, RoundFrameLayout roundFrameLayout);

    void onClickGetBitmapOverlay(Bitmap bitmap);

    void onClickGetEditTextChangeListener(StrokeTextView strokeTextView, RoundFrameLayout roundFrameLayout);

    void onClickGetGraphicViewListener(ImageView imageView, View view, View view2);

    void onClickGetImageViewListener(ImageView imageView, View view);

    void onEditTextChangeListener(View view, String str, int i);

    @Deprecated
    void onRemoveViewListener(int i);

    void onRemoveViewListener(ViewType viewType, int i);

    void onStartViewChangeListener(ViewType viewType);

    void onStopViewChangeListener(ViewType viewType);
}

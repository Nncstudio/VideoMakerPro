package com.video.maker.view;

import android.graphics.Paint;
import android.graphics.Path;

public class ClipPathManager implements ClipManager {
    private ClipPathCreator createClipPath;
    private final Paint paint;
    protected final Path path = new Path();

    public interface ClipPathCreator {
        Path createClipPath(int i, int i2);

        boolean requiresBitmap();
    }

    public ClipPathManager() {
        Paint paint2 = new Paint(1);
        this.paint = paint2;
        this.createClipPath = null;
        paint2.setColor(-16777216);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(1.0f);
    }

    public Paint getPaint() {
        return this.paint;
    }

    public boolean requiresBitmap() {
        ClipPathCreator clipPathCreator = this.createClipPath;
        return clipPathCreator != null && clipPathCreator.requiresBitmap();
    }


    public final Path createClipPath(int i, int i2) {
        ClipPathCreator clipPathCreator = this.createClipPath;
        if (clipPathCreator != null) {
            return clipPathCreator.createClipPath(i, i2);
        }
        return null;
    }

    public void setClipPathCreator(ClipPathCreator clipPathCreator) {
        this.createClipPath = clipPathCreator;
    }

    public Path createMask(int i, int i2) {
        return this.path;
    }

    public Path getShadowConvexPath() {
        return this.path;
    }

    public void setupClipLayout(int i, int i2) {
        this.path.reset();
        Path createClipPath2 = createClipPath(i, i2);
        if (createClipPath2 != null) {
            this.path.set(createClipPath2);
        }
    }
}

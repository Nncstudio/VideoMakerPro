package com.render.video.opengl.animations;


import com.render.video.opengl.GLESCanvas;

public abstract class CanvasAnim extends Animation {

    public abstract int getCanvasSaveFlags();

    public abstract void apply(GLESCanvas canvas);
}

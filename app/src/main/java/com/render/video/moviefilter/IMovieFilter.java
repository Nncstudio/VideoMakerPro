package com.render.video.moviefilter;

import com.render.video.PhotoMovie;
import com.render.video.opengl.FboTexture;


public interface IMovieFilter {
    void doFilter(PhotoMovie photoMovie,int elapsedTime, FboTexture inputTexture, FboTexture outputTexture);
    void release();
}

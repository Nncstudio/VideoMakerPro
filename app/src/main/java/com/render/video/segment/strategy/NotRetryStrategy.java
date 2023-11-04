package com.render.video.segment.strategy;

import com.render.video.PhotoMovie;
import com.render.video.model.PhotoData;
import com.render.video.segment.MovieSegment;

import java.util.List;

/**
 * Created by Administrator on 2015/6/12.
 */
public class NotRetryStrategy implements RetryStrategy {
    @Override
    public List<PhotoData> getAvailableData(PhotoMovie photoMovie, MovieSegment movieSegment) {
        return movieSegment==null?null:movieSegment.getAllocatedPhotos();
    }
}

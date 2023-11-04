package com.render.video.segment.strategy;

import com.render.video.PhotoMovie;
import com.render.video.model.PhotoData;
import com.render.video.segment.MovieSegment;

import java.util.List;

/**
 * Created by yellowcat on 2015/6/12.
 */
public interface RetryStrategy {
    List<PhotoData> getAvailableData(PhotoMovie photoMovie, MovieSegment movieSegment);
}

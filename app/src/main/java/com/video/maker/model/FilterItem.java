package com.video.maker.model;

import com.render.video.moviefilter.CameoMovieFilter;
import com.render.video.moviefilter.GrayMovieFilter;
import com.render.video.moviefilter.IMovieFilter;
import com.render.video.moviefilter.KuwaharaMovieFilter;
import com.render.video.moviefilter.LutMovieFilter;
import com.render.video.moviefilter.SnowMovieFilter;

public class FilterItem {
    public int imgRes;
    public String name;
    public FilterType type;

    public FilterItem(int i, String str, FilterType filterType) {
        this.imgRes = i;
        this.name = str;
        this.type = filterType;
    }

    public int getImgRes() {
        return this.imgRes;
    }

    public void setImgRes(int i) {
        this.imgRes = i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public FilterType getType() {
        return this.type;
    }

    public void setType(FilterType filterType) {
        this.type = filterType;
    }


    public IMovieFilter initFilter() {
        switch (type) {
            case GRAY:
                return new GrayMovieFilter();
            case SNOW:
                return new SnowMovieFilter();
            case CAMEO:
                return new CameoMovieFilter();
            case KUWAHARA:
                return new KuwaharaMovieFilter();
            case LUT1:
                return new LutMovieFilter(LutMovieFilter.LutType.A);
            case LUT2:
                return new LutMovieFilter(LutMovieFilter.LutType.B);
            case LUT3:
                return new LutMovieFilter(LutMovieFilter.LutType.C);
            case LUT4:
                return new LutMovieFilter(LutMovieFilter.LutType.D);
            case LUT5:
                return new LutMovieFilter(LutMovieFilter.LutType.E);
            default:
                return null;
        }
    }
}

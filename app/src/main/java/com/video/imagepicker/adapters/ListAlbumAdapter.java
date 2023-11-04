package com.video.imagepicker.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.video.imagepicker.activity.ImagePickerActivity;
import com.video.imagepicker.model.ImageModel;
import com.video.imagepicker.myinterface.OnListAlbum;
import com.video.maker.R;

import java.util.ArrayList;


public class ListAlbumAdapter extends ArrayAdapter<ImageModel> {
    Context context;
    ArrayList<ImageModel> data = new ArrayList();
    int layoutResourceId;
    OnListAlbum onListAlbum;
//    int pWidthItem = 0,  pHeightItem = 0;
    boolean counter;

    static class RecordHolder {
        ImageView click;
        ImageView imageItem;
        RelativeLayout layoutRoot;

        RecordHolder() {
        }
    }

    public ListAlbumAdapter(Context context, int layoutResourceId, ArrayList<ImageModel> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
//        this.pWidthItem = getDisplayInfo((Activity) context).widthPixels * 226 / 1080;
//        this.pHeightItem = getDisplayInfo((Activity) context).heightPixels * 226 / 1920;
        counter = true;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final RecordHolder holder;
        View row = convertView;
        if (row == null) {
            row = ((Activity) this.context).getLayoutInflater().inflate(this.layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.imageItem = (ImageView) row.findViewById(R.id.imageItem);
            holder.click = (ImageView) row.findViewById(R.id.click);
            holder.layoutRoot = (RelativeLayout) row.findViewById(R.id.layoutRoot);
//            holder.layoutRoot.getLayoutParams().height = this.pHeightItem;
//            holder.layoutRoot.getLayoutParams().width = this.pWidthItem;

//            holder.imageItem.getLayoutParams().width = this.pHeightItem;
//            holder.imageItem.getLayoutParams().height = this.pHeightItem;
//            holder.click.getLayoutParams().width = this.pHeightItem;
//            holder.click.getLayoutParams().height = this.pHeightItem;
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final ImageModel item = (ImageModel) this.data.get(position);
        //  Glide.with(this.context).load(item.getPathFile()).asBitmap().override((int) Callback.DEFAULT_DRAG_ANIMATION_DURATION, (int) Callback.DEFAULT_DRAG_ANIMATION_DURATION).animate(R.anim.anim_fade_in).thumbnail((float) AppConst.ZOOM_MIN).error(R.drawable.piclist_icon_default).fallback(R.drawable.piclist_icon_default).placeholder(R.drawable.piclist_icon_default).into(holder.imageItem);

        Glide.with(context).load(item.getPathFile()).placeholder(R.drawable.piclist_icon_default).into(holder.imageItem);

       // Picasso.with(this.context).load(new File(item.getPathFile())).placeholder(R.drawable.piclist_icon_default).into(holder.imageItem);


        holder.click.setVisibility(View.GONE);
        if (ImagePickerActivity.listItemSelect.size() > 0) {
            for (int i = 0; i < ImagePickerActivity.listItemSelect.size(); i++) {
                ImageModel item1 = ImagePickerActivity.listItemSelect.get(i);
                if (item1.getPathFile().equals(item.getPathFile())) {
                    holder.click.setVisibility(View.VISIBLE);
                }
            }
        }


        if (ImagePickerActivity.listItemSelect.size() < ImagePickerActivity.limitImageMax) {
            counter = true;
        }
        row.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ListAlbumAdapter.this.onListAlbum != null) {
                    ListAlbumAdapter.this.onListAlbum.OnItemListAlbumClick(item);
                }

                if (counter) {
                    holder.click.setVisibility(View.VISIBLE);
                }
                if (ImagePickerActivity.listItemSelect.size() == ImagePickerActivity.limitImageMax) {
                    counter = false;
                }
            }
        });
        return row;
    }

    public OnListAlbum getOnListAlbum() {
        return this.onListAlbum;
    }

    public void setOnListAlbum(OnListAlbum onListAlbum) {
        this.onListAlbum = onListAlbum;
    }

    public static DisplayMetrics getDisplayInfo(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }
}

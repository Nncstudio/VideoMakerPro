package com.video.maker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.video.maker.R;
import com.video.maker.model.MusicType;
import com.video.maker.util.KSUtil;
import com.video.maker.view.SquareImageView;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewholderMusic> {
    MusicAdapterListener listener;
    Context mContext;
    ArrayList<MusicType> musicTypeArrayList;
    int row_selected = -1;

    public interface MusicAdapterListener {
        void onMusicSelected(int i);
    }

    public MusicAdapter(ArrayList<MusicType> arrayList, Context context, MusicAdapterListener musicAdapterListener) {
        this.musicTypeArrayList = arrayList;
        this.mContext = context;
        this.listener = musicAdapterListener;
        this.row_selected = 0;
    }

    public ViewholderMusic onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewholderMusic(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music, viewGroup, false));
    }

    public void onBindViewHolder(ViewholderMusic viewholderMusic, int i) {


        if ((i + 1) >= 4 && !KSUtil.Musicposs.contains(i)) {
            viewholderMusic.premium.setVisibility(View.VISIBLE);
        } else {
            viewholderMusic.premium.setVisibility(View.GONE);
        }


        if (this.row_selected == i) {
            viewholderMusic.nameMusic.setText(this.musicTypeArrayList.get(i).getNameMusic());
//            Glide.with(this.mContext).load(Integer.valueOf(this.musicTypeArrayList.get(i).getImgMusic())).apply((BaseRequestOptions<?>) new RequestOptions().centerCrop()).into((ImageView) viewholderMusic.imgMusic);
            Glide.with(this.mContext).load(Integer.valueOf(this.musicTypeArrayList.get(i).getImgMusic())).into((ImageView) viewholderMusic.imgMusic);
            viewholderMusic.imgUnChecked.setImageResource(R.drawable.ic_music_check_shape);
            return;
        }
        viewholderMusic.nameMusic.setText(this.musicTypeArrayList.get(i).getNameMusic());
//        Glide.with(this.mContext).load(Integer.valueOf(this.musicTypeArrayList.get(i).getImgMusic())).apply((BaseRequestOptions<?>) new RequestOptions().centerCrop()).into((ImageView) viewholderMusic.imgMusic);
        Glide.with(this.mContext).load(Integer.valueOf(this.musicTypeArrayList.get(i).getImgMusic())).into((ImageView) viewholderMusic.imgMusic);
        viewholderMusic.imgUnChecked.setImageResource(R.drawable.ic_music_uncheck_shape);
    }

    public int getItemCount() {
        return this.musicTypeArrayList.size();
    }

    public class ViewholderMusic extends RecyclerView.ViewHolder {
        SquareImageView imgMusic;
        SquareImageView imgUnChecked;
        TextView nameMusic;

        ImageView premium;

        public ViewholderMusic(View view) {
            super(view);
            this.imgMusic = (SquareImageView) view.findViewById(R.id.imgMusic);
            this.imgUnChecked = (SquareImageView) view.findViewById(R.id.imgUnChecked);
            this.nameMusic = (TextView) view.findViewById(R.id.nameMusic);
            this.premium = view.findViewById(R.id.premium);
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    MusicAdapter.this.listener.onMusicSelected(ViewholderMusic.this.getAdapterPosition());
                    MusicAdapter.this.row_selected = ViewholderMusic.this.getAdapterPosition();
                    MusicAdapter.this.notifyDataSetChanged();

                }
            });
        }
    }

    public void setRowSelected(int i) {
        this.row_selected = i;
    }
}

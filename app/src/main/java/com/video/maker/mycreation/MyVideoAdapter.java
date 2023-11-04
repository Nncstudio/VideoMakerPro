package com.video.maker.mycreation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.video.maker.R;

import java.io.File;
import java.util.ArrayList;

public class MyVideoAdapter extends RecyclerView.Adapter<MyVideoAdapter.FileHolder>{

    ArrayList<String> videoFiles;
    Context context;
    CustomItemClickListener listener;

    public  static String txtpath;

    public MyVideoAdapter(ArrayList<String> fileList, Context context , CustomItemClickListener listener) {
        this.videoFiles = fileList;
        this.context = context;
        this.listener = listener;
    }

    public class FileHolder extends RecyclerView.ViewHolder{

        ImageView videoThumb, share, delete;
        ImageView play;
        RelativeLayout videoLay;
        RelativeLayout lay;

        TextView txt_path;

        public FileHolder(View itemView) {
            super(itemView);
            videoThumb = (ImageView) itemView.findViewById(R.id.videoThumbIV);
            share = (ImageView) itemView.findViewById(R.id.share);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            videoLay = (RelativeLayout) itemView.findViewById(R.id.videoLay);
            play = (ImageView) itemView.findViewById(R.id.play);
            lay = (RelativeLayout) itemView.findViewById(R.id.lay);
            txt_path = (TextView) itemView.findViewById(R.id.txt_path);

        }
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.myvideolay, parent, false);

        return new FileHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, @SuppressLint("RecyclerView") final int position) {


        Glide.with(context)
                .load(videoFiles.get(position))
                .into(holder.videoThumb);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(v, position));


        if (new File(videoFiles.get(position)).getAbsolutePath().contains(".mp4")) {
            holder.play.setVisibility(View.VISIBLE);
        } else {
            holder.play.setVisibility(View.GONE);
        }

        holder.share.setOnClickListener(v -> share(videoFiles.get(position)));

        holder.delete.setOnClickListener(v -> {
            new File(videoFiles.get(position)).delete();
            delete(position);
            Toast.makeText(context, "Delete Successfully!!!", Toast.LENGTH_SHORT).show();
        });

        holder.txt_path.setText(videoFiles.get(position));

        txtpath = videoFiles.get(position);

    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    public void delete(int position) {
        videoFiles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, videoFiles.size());
    }

    void share(String path) {

        MediaScannerConnection.scanFile(context, new String[]{Uri.fromFile(new File(path)).getPath()}, (String[]) null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String str, Uri uri) {
                Intent intent = new Intent("android.intent.action.SEND");
                if (new File(path).getAbsolutePath().contains(".mp4")) {
                    intent.setType("video/*");
                }else {
                    intent.setType("image/*");
                }
                intent.putExtra("android.intent.extra.SUBJECT", str);
                intent.putExtra("android.intent.extra.TITLE", str);
                intent.putExtra("android.intent.extra.STREAM", uri);
                intent.putExtra("txtpath", videoFiles.get(Integer.parseInt(path)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_this)));
            }
        });
    }
}

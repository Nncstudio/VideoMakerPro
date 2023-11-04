//package com.kessi.msvideomaker.mycreation;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.DefaultItemAnimator;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.hw.photomovie.inapppurchase.Prefs;
//import com.kessi.msvideomaker.R;
//import com.kessi.msvideomaker.activities.ShareActivity;
//import com.kessi.msvideomaker.activities.VideoPreviewActivity;
//import com.kessi.msvideomaker.util.AdManager;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public class MyAlbumActivity extends AppCompatActivity {
//
//    public static ArrayList<String> videoPath = new ArrayList<String>();
//
//    RecyclerView videoListView;
//    MyVideoAdapter videoAdapter;
//    int FLAG_VIDEO = 21;
//    ImageView backIV;
//    RelativeLayout header;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.my_videos);
//
//        header = (RelativeLayout) findViewById(R.id.header);
//        videoLoader();
//
//        backIV = (ImageView) findViewById(R.id.back);
//        backIV.setOnClickListener(v -> {
//            onBackPressed();
//        });
//        Prefs prefs = new Prefs(MyAlbumActivity.this);
//        if (prefs.getPremium() == 0) {
//            LinearLayout adContainer = findViewById(R.id.banner_container);
//            //MAX + Fb bidding Ads
//            AdManager.initMAX(MyAlbumActivity.this);
//            AdManager.maxBanner(MyAlbumActivity.this, adContainer, getResources().getColor(R.color.bg_color));
//        }
//    }
//
//
//    public void videoLoader() {
//        getFromStorage();
//        videoListView = (RecyclerView) findViewById(R.id.recyclerView);
//        videoAdapter = new MyVideoAdapter(videoPath, MyAlbumActivity.this, (v, position) -> {
//
//            Intent intent = new Intent(MyAlbumActivity.this, ShareActivity.class);
//            if (new File(videoPath.get(position)).getAbsolutePath().contains(".mp4")){
//                intent.putExtra("VIDEO", Uri.fromFile(new File(videoPath.get(position))));
//                intent.putExtra("ISPHOTO", "no");
//            }else {
//                intent.putExtra("PHOTO", Uri.fromFile(new File(videoPath.get(position))));
//                intent.putExtra("ISPHOTO", "yes");
//            }
//            startActivity(intent);
//        });
//
//        videoListView.setLayoutManager(new GridLayoutManager(this, 2));
//        videoListView.setItemAnimator(new DefaultItemAnimator());
//        videoListView.setAdapter(videoAdapter);
//
//    }
//
//    public void getFromStorage() {
//        String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                + "/VideoMaker";
//        File file = new File(folder);
//        videoPath = new ArrayList<String>();
//        if (file.isDirectory()) {
//            File[] listFile = file.listFiles();
//            Arrays.sort(listFile, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
//            for (int i = 0; i < listFile.length; i++) {
//                videoPath.add(listFile[i].getAbsolutePath());
//            }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == FLAG_VIDEO) {
//            videoAdapter.notifyDataSetChanged();
//            videoLoader();
//        }
//    }
//
//
//}

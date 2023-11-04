package com.video.maker.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.video.maker.R;
import com.video.maker.mycreation.MyVideoAdapter;
import com.video.maker.util.AdManager;
import com.video.maker.dialog.PrmsnDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.video.maker.util.KSUtil;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private long mLastClickTime = 0;

    public static ArrayList<String> videoPath = new ArrayList<String>();

    RecyclerView videoListView;
    MyVideoAdapter videoAdapter;
    int FLAG_VIDEO = 21;



    RelativeLayout rl_native_ad;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);

        addControls();
        videoLoader();




            rl_native_ad = findViewById(R.id.rl_native_ad);
            //MAX + Fb bidding Ads
            AdManager.initAD(MainActivity.this);
            AdManager.LoadInterstitalAd(MainActivity.this);
            AdManager.createNativeAdMAX(MainActivity.this, rl_native_ad);



    }


    public void videoLoader() {
        getFromStorage();
        videoListView = (RecyclerView) findViewById(R.id.recyclerView);
        Log.e( "videoLoader: ",""+videoPath );
        videoAdapter = new MyVideoAdapter(videoPath, MainActivity.this, (v, position) -> {

            Intent intent = new Intent(MainActivity.this, ShareActivity.class);
            if (new File(videoPath.get(position)).getAbsolutePath().contains(".mp4")){
                intent.putExtra("VIDEO", Uri.fromFile(new File(videoPath.get(position))));
                intent.putExtra("ISPHOTO", "no");
            }else {
                intent.putExtra("PHOTO", Uri.fromFile(new File(videoPath.get(position))));
                intent.putExtra("ISPHOTO", "yes");
            }
            startActivity(intent);
        });

        videoListView.setLayoutManager(new GridLayoutManager(this, 1));
        videoListView.setItemAnimator(new DefaultItemAnimator());
        videoListView.setAdapter(videoAdapter);

    }


    public void getFromStorage() {
        String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/VideoMaker";
        File file = new File(folder);
        videoPath = new ArrayList<String>();
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            Arrays.sort(listFile, (o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
            for (int i = 0; i < listFile.length; i++) {
                videoPath.add(listFile[i].getAbsolutePath());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FLAG_VIDEO) {
            videoAdapter.notifyDataSetChanged();
            videoLoader();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoLoader();
    }

    private void addControls() {
        findViewById(R.id.bt_new_project).setOnClickListener(this);
        findViewById(R.id.bt_rate).setOnClickListener(this);
        findViewById(R.id.bt_share).setOnClickListener(this);
//        findViewById(R.id.bt_creation).setOnClickListener(this);

        findViewById(R.id.bt_privacy).setOnClickListener(this);
        findViewById(R.id.bt_more).setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_new_project:

                KSUtil.Transitionposs.clear();
                KSUtil.Musicposs.clear();
                KSUtil.Filterposs.clear();
                if (SystemClock.elapsedRealtime() - this.mLastClickTime >= 1000) {
                    this.mLastClickTime = SystemClock.elapsedRealtime();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                        Dexter.withContext(this).withPermissions("android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO","android.permission.READ_MEDIA_AUDIO").withListener(new MultiplePermissionsListener() {
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    MainActivity.this.startActivity(new Intent(MainActivity.this, PickedImagesActivity.class));
                                }
                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                    PrmsnDialog.showSettingDialog(MainActivity.this);
                                }
                            }

                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).withErrorListener(new PermissionRequestErrorListener() {
                            public void onError(DexterError dexterError) {
                                Toast.makeText(MainActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        }).onSameThread().check();

                        return;
                    }
                    else {
                        Dexter.withContext(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    MainActivity.this.startActivity(new Intent(MainActivity.this, PickedImagesActivity.class));
                                }
                                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                    PrmsnDialog.showSettingDialog(MainActivity.this);
                                }
                            }

                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).withErrorListener(new PermissionRequestErrorListener() {
                            public void onError(DexterError dexterError) {
                                Toast.makeText(MainActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        }).onSameThread().check();
                    }
                    return;
                }
                return;


            case R.id.bt_policy:
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getString(R.string.policy_url))));
                return;
            case R.id.bt_rate:
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                return;
            case R.id.bt_privacy:
                startActivity(new Intent(MainActivity.this, PrivacyActivity.class));
                return;
            case R.id.bt_more:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=7081479513420377164&hl=en")));
                return;
            case R.id.bt_share:
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    intent.putExtra("android.intent.extra.SUBJECT", "My application name");
                    intent.putExtra("android.intent.extra.TEXT", "\nLet me recommend you this application\n\n" + "\"https://play.google.com/store/apps/details?id=" + getPackageName());
                    startActivity(Intent.createChooser(intent, "Choose one"));
                    return;
                } catch (Exception unused) {
                    return;
                }
            default:
                return;
        }
    }



    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }





}

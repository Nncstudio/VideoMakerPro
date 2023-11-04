package com.video.maker.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.video.maker.R;
import com.video.maker.mycreation.MyVideoAdapter;
import com.video.maker.util.AdManager;
import com.video.maker.util.Config;

public class ShareActivity extends BaseActivity implements View.OnClickListener {

    TextView txtpath;
    Uri uri = null;
    private ImageView videoThumbnail, playImg;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_share);
        inIt();
        loadVideoThumb();


        RelativeLayout rl_native_ad = findViewById(R.id.rl_native_ad);
        //MAX + Fb bidding Ads
        AdManager.initAD(ShareActivity.this);
        AdManager.createNativeAdMAX(ShareActivity.this, rl_native_ad);


        txtpath = findViewById(R.id.txt_path);
        txtpath.setText(MyVideoAdapter.txtpath);
    }


    private void loadVideoThumb() {
        Uri uri2;
        if (getIntent().getStringExtra("ISPHOTO").equals("no")) {
            uri2 = (Uri) getIntent().getParcelableExtra("VIDEO");
            playImg.setVisibility(View.VISIBLE);
            this.videoThumbnail.setImageBitmap(ThumbnailUtils.createVideoThumbnail(uri2.getPath(), 1));
        } else {
            uri2 = (Uri) getIntent().getParcelableExtra("PHOTO");
            playImg.setVisibility(View.GONE);
            Glide.with(this)
                    .load(uri2)
                    .into(videoThumbnail);

        }
        this.uri = uri2;

    }


    private void inIt() {
        ImageView imageView = (ImageView) findViewById(R.id.videoView_thumbnail);
        this.videoThumbnail = imageView;
        playImg = findViewById(R.id.playImg);
        imageView.setOnClickListener(this);
        findViewById(R.id.btnShareMore).setOnClickListener(this);
        findViewById(R.id.btnInstagram).setOnClickListener(this);
        findViewById(R.id.btnFacebook).setOnClickListener(this);
        findViewById(R.id.btnMessenger).setOnClickListener(this);
        findViewById(R.id.btnYoutube).setOnClickListener(this);
        findViewById(R.id.btnGmail).setOnClickListener(this);
        findViewById(R.id.btnWhatsApp).setOnClickListener(this);
        findViewById(R.id.btnTwitter).setOnClickListener(this);
//        findViewById(R.id.btn_new).setOnClickListener(this);
        findViewById(R.id.btnBackFinal).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBackFinal:
                super.onBackPressed();
                return;
            case R.id.btnFacebook:
                shareVideo(getString(R.string.app_name_string), this.uri.getPath(), Config.FACE);
                return;
            case R.id.btnGmail:
                shareVideo(getString(R.string.app_name_string), this.uri.getPath(), Config.GMAIL);
                return;
            case R.id.btnInstagram:
                shareVideo(getString(R.string.app_name_string), this.uri.getPath(), Config.INSTA);
                return;
            case R.id.btnMessenger:
                shareVideo(getString(R.string.app_name_string), this.uri.getPath(), Config.MESSEGER);
                return;

            case R.id.btnShareMore:
                shareMore(getString(R.string.app_name_string), this.uri.getPath());
                return;
            case R.id.btnTwitter:
                shareVideo(getString(R.string.app_name_string), this.uri.getPath(), Config.TWITTER);
                return;
            case R.id.btnWhatsApp:
                shareVideo(getString(R.string.app_name_string), this.uri.getPath(), Config.WHATSAPP);
                return;
            case R.id.btnYoutube:
                shareVideo(getString(R.string.app_name_string), this.uri.getPath(), Config.YOUTU);
                return;
//            case R.id.btn_new:
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                return;
            case R.id.videoView_thumbnail:
                if (getIntent().getStringExtra("ISPHOTO").equals("no")) {
                    Intent intent2 = new Intent(this, VideoPlayerActivity.class);
                    intent2.putExtra("VIDEO", this.uri);
                    startActivity(intent2);
                }
                return;
            default:
                return;
        }
    }

    public void shareMore(final String str, String str2) {
        MediaScannerConnection.scanFile(this, new String[]{str2}, (String[]) null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String str, Uri uri) {
                Intent intent = new Intent("android.intent.action.SEND");
                if (getIntent().getStringExtra("ISPHOTO").equals("no")) {
                    intent.setType("video/*");
                } else {
                    intent.setType("image/*");
                }
                intent.putExtra("android.intent.extra.SUBJECT", str);
                intent.putExtra("android.intent.extra.TITLE", str);
                intent.putExtra("android.intent.extra.STREAM", uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                ShareActivity finalActivity = ShareActivity.this;
                finalActivity.startActivity(Intent.createChooser(intent, finalActivity.getString(R.string.share_this)));
            }
        });
    }

    public void shareVideo(final String str, String str2, final String str3) {
        if (isPackageInstalled(this, str3)) {
            MediaScannerConnection.scanFile(this, new String[]{str2}, (String[]) null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String str, Uri uri) {
                    Intent intent = new Intent("android.intent.action.SEND");
                    if (getIntent().getStringExtra("ISPHOTO").equals("no")) {
                        intent.setType("video/*");
                    } else {
                        intent.setType("image/*");
                    }
                    intent.putExtra("android.intent.extra.SUBJECT", str);
                    intent.putExtra("android.intent.extra.TITLE", str);
                    intent.putExtra("android.intent.extra.STREAM", uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    intent.setPackage(str3);
                    ShareActivity.this.startActivity(intent);
                }
            });
            return;
        }
        Toast.makeText(this, getString(R.string.app_not_install), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("market://details?id=" + str3));
        startActivity(intent);
    }

    @SuppressLint("WrongConstant")
    public static boolean isPackageInstalled(Context context, String str) {
        try {
            context.getPackageManager().getPackageInfo(str, 128);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }


    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}

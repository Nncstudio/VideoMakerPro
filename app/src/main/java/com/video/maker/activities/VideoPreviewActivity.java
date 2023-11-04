package com.video.maker.activities;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.cardview.widget.CardView;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.util.GmsVersion;
import com.google.android.material.tabs.TabLayout;
import com.render.video.PhotoMovie;
import com.render.video.PhotoMovieFactory;
import com.render.video.PhotoMoviePlayer;

import com.render.video.model.PhotoSource;
import com.render.video.model.SimplePhotoData;
import com.render.video.music.MusicPlayer;
import com.render.video.record.GLMovieRecorder;
import com.render.video.render.GLSurfaceMovieRenderer;
import com.render.video.render.GLTextureMovieRender;
import com.render.video.render.GLTextureView;
import com.render.video.timer.IMovieTimer;
import com.render.video.util.MLog;
import com.video.maker.R;
import com.video.maker.util.UriUtil;
import com.video.maker.adapter.ViewPagerAdapter;
import com.video.maker.util.AdManager;
import com.video.maker.dialog.ExitDialog;
import com.video.maker.fragment.movie.DurationFragment;
import com.video.maker.fragment.movie.FilterFragment;
import com.video.maker.fragment.movie.MusicFragment;
import com.video.maker.fragment.movie.RatioFragment;
import com.video.maker.fragment.movie.TransitionFragment;
import com.video.maker.util.DeviceUtils;
import com.video.maker.model.FilterItem;
import com.video.maker.model.TransferItem;
import com.video.maker.view.PlayPauseView;
import com.video.maker.view.radioview.RatioDatumMode;
import com.video.maker.view.radioview.RatioFrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

public class VideoPreviewActivity extends BaseActivity implements View.OnClickListener, IMovieTimer.MovieListener, FilterFragment.FilterFragmentListener, TransitionFragment.TransferFragmentListener, MusicFragment.MusicFragmentListener, DurationFragment.DurationFragmentListener, RatioFragment.RatioFragmentListener {
    private static final int REQUEST_MUSIC = 234;

    public static final String TAG = VideoPreviewActivity.class.getSimpleName();
    private Runnable alphaRunnable = new Runnable() {
        public void run() {
            VideoPreviewActivity.this.controlContainer.animate().alpha(0.0f).setListener(new Animator.AnimatorListener() {
                public void onAnimationCancel(Animator animator) {
                }

                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    VideoPreviewActivity.this.controlContainer.setVisibility(View.GONE);
                }
            }).start();
        }
    };
    String audioPath = null;
    ImageView btnBack;
    CardView btnFinish;
    PlayPauseView btnPlayPause;

    public ViewGroup controlContainer;
    int duration = 2000;
    private Handler handler = new Handler();
    private boolean isSeekBarTracking = false;

    private GLTextureView mGLTextureView;
    private GLSurfaceMovieRenderer mMovieRenderer;
    private PhotoMovieFactory.PhotoMovieType mMovieType = PhotoMovieFactory.PhotoMovieType.GRADIENT;
    private Uri mMusicUri;
    private PhotoMovie mPhotoMovie;

    public PhotoMoviePlayer mPhotoMoviePlayer;
    String musicPath = null;
    String path = null;

    public ProgressDialog progressDialog;
    RatioFrameLayout ratioFrameLayout;

    public ViewGroup savingLayout;
    private SeekBar sbControl;
    private TabLayout tabLayoutMovie;
    private TextView tvControlCurrentTime;
    private TextView tvControlTotalTime;

    public TextView tvSaving;
    private ViewPager viewPagerMovie;


    public void onMovieEnd() {
    }

    public void onMoviePaused() {
    }

    public void onMovieResumed() {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_video_preview);
        ProgressDialog progressDialog2 = new ProgressDialog(this);
        this.progressDialog = progressDialog2;
        progressDialog2.setMessage("Loading...");
        this.progressDialog.setCanceledOnTouchOutside(false);


        initView();
        this.btnPlayPause.setPlayPauseListener(new PlayPauseView.PlayPauseListener() {
            public void play() {
                VideoPreviewActivity.this.onResumeVideo();
            }

            public void pause() {
                VideoPreviewActivity.this.onPauseVideo();
            }
        });
        ArrayList<String> stringArrayList = getIntent().getExtras().getStringArrayList("PHOTO");
        int[] iArr = {R.raw.love, R.raw.friend, R.raw.beach, R.raw.travel, R.raw.christmas, R.raw.happy, R.raw.movie, R.raw.positive, R.raw.summer};
        for (int i = 0; i < 9; i++) {
            this.path = Environment.getExternalStorageDirectory() + "/Download/StoryMaker";
            File file = new File(this.path);
            if (file.mkdirs() || file.isDirectory()) {
                String str = i + ".m4r";
                try {
                    CopyRAWtoSDCard(iArr[i], this.path + File.separator + str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        getMusicData(0);
        initMoviePlayer();
        onPhotoPick(stringArrayList);


        RelativeLayout rl_native_ad = findViewById(R.id.rl_native_ad);
        //MAX + Fb bidding Ads
        AdManager.initAD(VideoPreviewActivity.this);
        AdManager.LoadInterstitalAd(VideoPreviewActivity.this);
        AdManager.createNativeAdMAX(VideoPreviewActivity.this, rl_native_ad);

    }


    void startActivityes(Intent intent, int reqCode) {
        AdManager.adCounter++;
        AdManager.showMaxInterstitial(VideoPreviewActivity.this, intent, reqCode);
    }

    private void initView() {
        this.ratioFrameLayout = (RatioFrameLayout) findViewById(R.id.videoContainer);
        this.mGLTextureView = (GLTextureView) findViewById(R.id.gl_texture);
        this.btnPlayPause = (PlayPauseView) findViewById(R.id.btn_play_pause);
        ImageView imageView = (ImageView) findViewById(R.id.btnBack);
        this.btnBack = imageView;
        imageView.setOnClickListener(this);
        CardView imageView2 = findViewById(R.id.btnFinish);
        this.btnFinish = imageView2;
        imageView2.setOnClickListener(this);
        this.mGLTextureView.setOnClickListener(this);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerMovie);
        this.viewPagerMovie = viewPager;
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayoutMovie);
        this.tabLayoutMovie = tabLayout;
        tabLayout.setupWithViewPager(this.viewPagerMovie);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

//                startActivityes(null, 0);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setupTabIconMovie();
        this.tabLayoutMovie.setTabTextColors(getResources().getColor(R.color.tab_white), getResources().getColor(R.color.white));

        this.btnPlayPause.setPlaying(true);
        this.controlContainer = (ViewGroup) findViewById(R.id.control_container);
        this.sbControl = (SeekBar) findViewById(R.id.sb_control);
        this.tvControlCurrentTime = (TextView) findViewById(R.id.tv_control_current_time);
        this.tvControlTotalTime = (TextView) findViewById(R.id.tv_control_total_time);
        this.sbControl.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        this.savingLayout = (ViewGroup) findViewById(R.id.saving_layout);
        this.tvSaving = (TextView) findViewById(R.id.tv_saving);
        hideControl();
    }

    private void setupTabIconMovie() {

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_selected}, // selected state
                new int[]{-android.R.attr.state_selected} // unselected state
        };
        @ColorInt int selectedColor = Color.parseColor("#7555FB"); // Replace with your desired color for selected state
        @ColorInt int unselectedColor = Color.parseColor("#FFFFFF"); // Replace with your desired color for unselected state
        int[] colors = new int[]{
                selectedColor,
                unselectedColor
        };
        ColorStateList tabColors = new ColorStateList(states, colors);


        ImageView textView = (ImageView) LayoutInflater.from(this).inflate(R.layout.main_tab, (ViewGroup) null);
//        textView.setText(getString(R.string.str_filter));
//        textView.setTextColor(color);
        textView.setImageResource(R.drawable.ic_movie_filter);
        this.tabLayoutMovie.getTabAt(0).setCustomView((View) textView);
        ImageView textView2 = (ImageView) LayoutInflater.from(this).inflate(R.layout.main_tab, (ViewGroup) null);
//        textView2.setText(getString(R.string.str_transfer));
//        textView2.setTextColor(color);
        textView2.setImageResource(R.drawable.ic_movie_transfer);
        this.tabLayoutMovie.getTabAt(1).setCustomView((View) textView2);
        ImageView textView3 = (ImageView) LayoutInflater.from(this).inflate(R.layout.main_tab, (ViewGroup) null);
//        textView3.setText(getString(R.string.str_music));
//        textView3.setTextColor(color);
        textView3.setImageResource(R.drawable.ic_movie_music);
        this.tabLayoutMovie.getTabAt(2).setCustomView((View) textView3);
        ImageView textView4 = (ImageView) LayoutInflater.from(this).inflate(R.layout.main_tab, (ViewGroup) null);
//        textView4.setText(getString(R.string.str_duration));
//        textView4.setTextColor(color);
        textView4.setImageResource(R.drawable.ic_duration_movie);
        this.tabLayoutMovie.getTabAt(3).setCustomView((View) textView4);
        ImageView textView5 = (ImageView) LayoutInflater.from(this).inflate(R.layout.main_tab, (ViewGroup) null);
//        textView5.setText(getString(R.string.str_ratio));
//        textView5.setTextColor(color);
        textView5.setImageResource(R.drawable.ic_aspect_ratio);
        this.tabLayoutMovie.getTabAt(4).setCustomView((View) textView5);
        if ((getResources().getConfiguration().screenLayout & 15) == 1) {
            this.tabLayoutMovie.setTabMode(TabLayout.MODE_SCROLLABLE);
        }

        ImageViewCompat.setImageTintList(textView, tabColors);
        ImageViewCompat.setImageTintList(textView2, tabColors);
        ImageViewCompat.setImageTintList(textView3, tabColors);
        ImageViewCompat.setImageTintList(textView4, tabColors);
        ImageViewCompat.setImageTintList(textView5, tabColors);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        FilterFragment filterFragment = new FilterFragment();
        viewPagerAdapter.addFrag(filterFragment, getString(R.string.str_filter));
        filterFragment.setFilterFragmentListener(this);
        TransitionFragment transferFragment = new TransitionFragment();
        viewPagerAdapter.addFrag(transferFragment, getString(R.string.str_transfer));
        transferFragment.setTransferFragmentListener(this);
        MusicFragment musicFragment = new MusicFragment();
        viewPagerAdapter.addFrag(musicFragment, getString(R.string.str_music));
        musicFragment.setMusicFragmentListener(this);
        DurationFragment durationFragment = new DurationFragment();
        viewPagerAdapter.addFrag(durationFragment, getString(R.string.str_duration));
        durationFragment.setDurationFragmentListener(this);
        RatioFragment ratioFragment = new RatioFragment();
        viewPagerAdapter.addFrag(ratioFragment, getString(R.string.str_ratio));
        ratioFragment.RatioFragmentListener(this);
        viewPager.setAdapter(viewPagerAdapter);
    }

    private void initMoviePlayer() {
        this.mMovieRenderer = new GLTextureMovieRender(this.mGLTextureView);
        PhotoMoviePlayer photoMoviePlayer = new PhotoMoviePlayer(getApplicationContext());
        this.mPhotoMoviePlayer = photoMoviePlayer;
        photoMoviePlayer.setMovieRenderer(this.mMovieRenderer);
        this.mPhotoMoviePlayer.setMovieListener(this);
        this.mPhotoMoviePlayer.setLoop(true);
        this.mPhotoMoviePlayer.setOnPreparedListener(new PhotoMoviePlayer.OnPreparedListener() {
            public void onPreparing(PhotoMoviePlayer photoMoviePlayer, float f) {
            }

            public void onPrepared(PhotoMoviePlayer photoMoviePlayer, int i, int i2) {
                VideoPreviewActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        VideoPreviewActivity.this.mPhotoMoviePlayer.start();
                    }
                });
            }

            public void onError(PhotoMoviePlayer photoMoviePlayer) {
                MLog.i("onPrepare", "onPrepare error");
            }
        });
    }

    public void onPhotoPick(ArrayList<String> arrayList) {
        ArrayList arrayList2 = new ArrayList(arrayList.size());
        Iterator<String> it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(new SimplePhotoData(this, it.next(), 2));
        }
        PhotoSource photoSource = new PhotoSource(arrayList2);
        if (this.mPhotoMoviePlayer == null) {
            startPlay(photoSource);
        } else {
            createVideo(photoSource, PhotoMovieFactory.PhotoMovieType.GRADIENT);
        }
    }


    public void startVideo() {
        this.mPhotoMoviePlayer.start();
        int duration2 = this.mPhotoMovie.getDuration() / 1000;
        this.tvControlTotalTime.setText(DeviceUtils.getTimeString(duration2));
        this.sbControl.setMax(duration2);
    }

    private File initVideoFile() {
        File externalStoragePublicDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/VideoMaker");
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory.mkdirs();
        }
        if (!externalStoragePublicDirectory.exists()) {
            externalStoragePublicDirectory = getCacheDir();
        }
        return new File(externalStoragePublicDirectory, String.format("Story_maker_%s.mp4", new Object[]{new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(Long.valueOf(System.currentTimeMillis()))}));
    }

    public void onPauseVideo() {
        this.mPhotoMoviePlayer.pause();
    }

    public void onResumeVideo() {
        this.mPhotoMoviePlayer.start();
    }

    private void startPlay(PhotoSource photoSource) {
        PhotoMovie generatePhotoMovie = PhotoMovieFactory.generatePhotoMovie(photoSource, this.mMovieType, this.duration);
        this.mPhotoMovie = generatePhotoMovie;
        this.mPhotoMoviePlayer.setDataSource(generatePhotoMovie);
        this.mPhotoMoviePlayer.prepare();
    }

    public void saveVideo() {
        this.mPhotoMoviePlayer.pause();

        this.savingLayout.setVisibility(View.VISIBLE);
        TextView textView = this.tvSaving;
        textView.setText(getString(R.string.str_saving_video) + "...");
        final long currentTimeMillis = System.currentTimeMillis();
        GLMovieRecorder gLMovieRecorder = new GLMovieRecorder(this);
        final File initVideoFile = initVideoFile();
        MLog.i(TAG, "saveVideo initVideoFile");
        gLMovieRecorder.configOutput(this.mGLTextureView.getWidth(), this.mGLTextureView.getHeight(), this.mGLTextureView.getWidth() * this.mGLTextureView.getHeight() > 1500000 ? GmsVersion.VERSION_SAGA : 4000000, 30, 1, initVideoFile.getAbsolutePath());
        PhotoMovie generatePhotoMovie = PhotoMovieFactory.generatePhotoMovie(this.mPhotoMovie.getPhotoSource(), this.mMovieType, this.duration);
        GLSurfaceMovieRenderer gLSurfaceMovieRenderer = new GLSurfaceMovieRenderer(this.mMovieRenderer);
        gLSurfaceMovieRenderer.setPhotoMovie(generatePhotoMovie);
        if (this.mMusicUri != null) {
            this.audioPath = this.musicPath;
            Log.e( "onTypeMusic mp ", ""+musicPath);
            Log.e( "onTypeMusic op ", ""+audioPath);
            Log.e( "onTypeMusic mu ", ""+mMusicUri);

        }
        if (!TextUtils.isEmpty(this.audioPath)) {
            if (Build.VERSION.SDK_INT < 18) {
                Toast.makeText(getApplicationContext(), "Mix audio needs api18!", Toast.LENGTH_LONG).show();
            } else {
                gLMovieRecorder.setMusic(this.audioPath);

            }
        }
        gLMovieRecorder.setDataSource(gLSurfaceMovieRenderer);
        MLog.i(TAG, "saveVideo setDataSource");
        final GLMovieRecorder gLMovieRecorder2 = gLMovieRecorder;
        gLMovieRecorder.startRecord(new GLMovieRecorder.OnRecordListener() {
            public void onRecordFinish(boolean z) {
                File file = initVideoFile;
                long currentTimeMillis = System.currentTimeMillis();
                String access$100 = VideoPreviewActivity.TAG;
                MLog.i(access$100, "onRecordFinish:" + (currentTimeMillis - currentTimeMillis));
                if (z) {
                    VideoPreviewActivity.this.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(initVideoFile)));
                    Intent intent = new Intent(VideoPreviewActivity.this, ShareActivity.class);
                    intent.putExtra("VIDEO", Uri.fromFile(file));
                    intent.putExtra("ISPHOTO", "no");

                    AdManager.adCounter = AdManager.adDisplayCounter - 1;
                    VideoPreviewActivity.this.startActivityes(intent, 0);

                } else {
                    Toast.makeText(VideoPreviewActivity.this.getApplicationContext(), "Record error!", Toast.LENGTH_LONG).show();
                }
                if (gLMovieRecorder2.getAudioRecordException() != null) {
                    Log.e("XXXXXXXX", gLMovieRecorder2.getAudioRecordException().getMessage());
                    Context applicationContext = VideoPreviewActivity.this.getApplicationContext();
                    Toast.makeText(applicationContext, "record audio failed:" + gLMovieRecorder2.getAudioRecordException().toString(), Toast.LENGTH_LONG).show();
                }
                VideoPreviewActivity.this.savingLayout.setVisibility(View.GONE);
            }

            public void onRecordProgress(final int i, final int i2) {
                VideoPreviewActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        TextView access$300 = VideoPreviewActivity.this.tvSaving;
                        int per = ((int) ((((float) i) / ((float) i2)) * 100.0f));
                        if (per > 100) {
                            per = 100;
                        }
                        access$300.setText(VideoPreviewActivity.this.getString(R.string.str_saving_video) + " " + per + "%");
                    }
                });
            }
        });
    }

    public void setMusic(Uri uri) {
        this.mMusicUri = uri;
        createVideo(this.mPhotoMovie.getPhotoSource(), this.mMovieType);

    }

    public void createVideo(PhotoSource photoSource, PhotoMovieFactory.PhotoMovieType photoMovieType) {
        this.progressDialog.show();
        this.mPhotoMoviePlayer.stop();
        PhotoMovie generatePhotoMovie = PhotoMovieFactory.generatePhotoMovie(photoSource, photoMovieType, this.duration);
        this.mPhotoMovie = generatePhotoMovie;
        this.mPhotoMoviePlayer.setDataSource(generatePhotoMovie);
        this.mPhotoMoviePlayer.setOnPreparedListener(new PhotoMoviePlayer.OnPreparedListener() {
            public void onPreparing(PhotoMoviePlayer photoMoviePlayer, float f) {
                Log.d("onPrepare", "onPreparing " + f);
            }

            public void onPrepared(PhotoMoviePlayer photoMoviePlayer, int i, int i2) {
                Log.d("onPrepare", "onPrepared " + i2);
                VideoPreviewActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        VideoPreviewActivity.this.startVideo();
                        if (VideoPreviewActivity.this.progressDialog != null) {
                            VideoPreviewActivity.this.progressDialog.dismiss();
                        }
                    }
                });
            }

            public void onError(PhotoMoviePlayer photoMoviePlayer) {
                if (VideoPreviewActivity.this.progressDialog != null) {
                    VideoPreviewActivity.this.progressDialog.dismiss();
                }
                Log.d("onPrepare", "onPrepare onError");
            }
        });
//        MovieActivity.this.mPhotoMoviePlayer.prepare();
        if (this.mMusicUri != null) {
            ProgressDialog progressDialog2 = this.progressDialog;
            if (progressDialog2 != null) {
                progressDialog2.show();
            }
            this.mPhotoMoviePlayer.setMusic(this, this.mMusicUri, new MusicPlayer.OnMusicOKListener() {
                public void onMusicOK() {
                    VideoPreviewActivity.this.mPhotoMoviePlayer.prepare();
                }
            });
        }
    }


    private void CopyRAWtoSDCard(int id, String path) throws IOException {
        InputStream in = getResources().openRawResource(id);
        FileOutputStream out = new FileOutputStream(path);
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnBack) {
            ExitDialog photoDiscardDialog = new ExitDialog(this);
            photoDiscardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            photoDiscardDialog.show();
        } else if (id == R.id.btnFinish) {
            saveVideo();
//            AdmobAds.showFullAds((AdmobAds.OnAdsCloseListener) null);
        } else if (id == R.id.gl_texture) {
            this.handler.removeCallbacks(this.alphaRunnable);
            this.controlContainer.setAlpha(1.0f);
            this.controlContainer.setVisibility(View.VISIBLE);
            hideControl();
        }
    }

    private void hideControl() {
        this.handler.removeCallbacks(this.alphaRunnable);
        this.handler.postDelayed(this.alphaRunnable, 3500);
    }

    public void onFilter(FilterItem filterItem) {
        this.mMovieRenderer.setMovieFilter(filterItem.initFilter());
    }

    public void onMyMusic() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(intent, REQUEST_MUSIC);
    }

    public void onTypeMusic(int i) {
        getMusicData(i);
        Log.e( "onTypeMusic: ", ""+i);
        setMusic(this.mMusicUri);
        Log.e( "onTypeMusic o mp ", ""+musicPath);
        Log.e( "onTypeMusic o op ", ""+audioPath);
        Log.e( "onTypeMusic o mu ", ""+mMusicUri);
    }

    private void getMusicData(int i) {
//        Log.e("Raw songs", "song id & name: " +getResources().getIdentifier("love", "raw", getPackageName()) + " " + "love");
//        Log.e("Raw songs", "song id & name: " +getResources().getIdentifier("friend", "raw", getPackageName()) + " " + "friend");
//        Log.e("Raw songs", "song id & name: " +getResources().getIdentifier("christmas", "raw", getPackageName()) + " " + "christmas");
//        Log.e("Raw songs", "song id & name: " +getResources().getIdentifier("positive", "raw", getPackageName()) + " " + "positive");
//        Log.e("Raw songs", "song id & name: " +getResources().getIdentifier("movie", "raw", getPackageName()) + " " + "movie");
//        Log.e("Raw songs", "song id & name: " +getResources().getIdentifier("beach", "raw", getPackageName()) + " " + "beach");
//        Log.e("Raw songs", "song id & name: " +getResources().getIdentifier("summer", "raw", getPackageName()) + " " + "summer");
//        Log.e("Raw songs", "song id & name: " +getResources().getIdentifier("travel", "raw", getPackageName()) + " " + "travel");
//        Log.e("Raw songs", "song id & name: " +getResources().getIdentifier("happy", "raw", getPackageName()) + " " + "happy");
        switch (i) {
            case 0:
                this.mMusicUri = Uri.parse("android.resource://com.video.maker/" + getResources().getIdentifier("love", "raw", getPackageName()));
                this.musicPath = this.path + File.separator + "0.m4r";
                return;
            case 1:
                this.mMusicUri = Uri.parse("android.resource://com.video.maker/" + getResources().getIdentifier("friend", "raw", getPackageName()));
                this.musicPath = this.path + File.separator + "1.m4r";
                return;
            case 4:
                this.mMusicUri = Uri.parse("android.resource://com.video.maker/" + getResources().getIdentifier("movie", "raw", getPackageName()));
                this.musicPath = this.path + File.separator + "4.m4r";
                return;
            case 7:
                this.mMusicUri = Uri.parse("android.resource://com.video.maker/" + getResources().getIdentifier("travel", "raw", getPackageName()));
                this.musicPath = this.path + File.separator + "7.m4r";
                return;
            case 6:
                this.mMusicUri = Uri.parse("android.resource://com.video.maker/" + getResources().getIdentifier("summer", "raw", getPackageName()));
                this.musicPath = this.path + File.separator + "6.m4r";
                return;
            case 2:
                this.mMusicUri = Uri.parse("android.resource://com.video.maker/" + getResources().getIdentifier("christmas", "raw", getPackageName()));
                this.musicPath = this.path + File.separator + "2.m4r";
                return;
            case 8:
                this.mMusicUri = Uri.parse("android.resource://com.video.maker/" + getResources().getIdentifier("happy", "raw", getPackageName()));
                this.musicPath = this.path + File.separator + "8.m4r";
                return;
            case 3:
                this.mMusicUri = Uri.parse("android.resource://com.video.maker/" + getResources().getIdentifier("positive", "raw", getPackageName()));
                this.musicPath = this.path + File.separator + "3.m4r";
                return;
            case 5:
                this.mMusicUri = Uri.parse("android.resource://com.video.maker/" + getResources().getIdentifier("beach", "raw", getPackageName()));
                this.musicPath = this.path + File.separator + "5.m4r";
                return;
            default:
                return;
        }
    }

    public void onTransfer(TransferItem transferItem) {
        this.mMovieType = transferItem.type;
        createVideo(this.mPhotoMovie.getPhotoSource(), this.mMovieType);
    }

    public void onDurationSelect(int i) {
        this.duration = i;
        createVideo(this.mPhotoMovie.getPhotoSource(), this.mMovieType);
    }

    public void onMovieUpdate(int i) {
        if (!this.isSeekBarTracking) {
            int round = Math.round(((float) i) / 1000.0f);
            this.sbControl.setProgress(round);
            this.tvControlCurrentTime.setText(DeviceUtils.getTimeString(round));
        }
    }

    public void onMovieStarted() {
        MLog.d(TAG, "onMovieStarted");
    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == REQUEST_MUSIC) {
            setMusic(intent.getData());
            this.musicPath = UriUtil.getPath(this, this.mMusicUri);
        }
    }


    public void onPostResume() {
        super.onPostResume();
    }


    public void onPause() {
        super.onPause();
    }


    public void onStop() {
        super.onStop();
        PhotoMoviePlayer photoMoviePlayer = this.mPhotoMoviePlayer;
        if (photoMoviePlayer != null) {
            photoMoviePlayer.pause();

        }
    }


    public void onStart() {
        super.onStart();
        PhotoMoviePlayer photoMoviePlayer = this.mPhotoMoviePlayer;
        if (photoMoviePlayer != null) {
            photoMoviePlayer.start();
        }
    }

    public void onBackPressed() {
        ExitDialog photoDiscardDialog = new ExitDialog(this);
        photoDiscardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        photoDiscardDialog.show();
    }

    public void destroyMovie() {
        PhotoMoviePlayer photoMoviePlayer = this.mPhotoMoviePlayer;
        if (photoMoviePlayer != null) {
            photoMoviePlayer.pause();
            this.mPhotoMoviePlayer.destroy();
            Intent intent = new Intent(VideoPreviewActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void onRatio(int i) {
        if (i == 11) {
            this.ratioFrameLayout.setRatio(RatioDatumMode.valueOf(1), 1.0f, 1.0f);
            createVideo(this.mPhotoMovie.getPhotoSource(), this.mMovieType);
        } else if (i == 34) {
            this.ratioFrameLayout.setRatio(RatioDatumMode.valueOf(1), 3.0f, 4.0f);
            createVideo(this.mPhotoMovie.getPhotoSource(), this.mMovieType);
        } else if (i == 43) {
            this.ratioFrameLayout.setRatio(RatioDatumMode.valueOf(1), 4.0f, 3.0f);
            createVideo(this.mPhotoMovie.getPhotoSource(), this.mMovieType);
        } else if (i == 169) {
            this.ratioFrameLayout.setRatio(RatioDatumMode.valueOf(1), 16.0f, 9.0f);
            createVideo(this.mPhotoMovie.getPhotoSource(), this.mMovieType);
        } else if (i == 916) {
            this.ratioFrameLayout.setRatio(RatioDatumMode.valueOf(1), 9.0f, 16.0f);
            createVideo(this.mPhotoMovie.getPhotoSource(), this.mMovieType);
        }
    }


    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}

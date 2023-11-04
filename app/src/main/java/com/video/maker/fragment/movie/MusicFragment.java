package com.video.maker.fragment.movie;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.video.maker.R;
import com.video.maker.activities.VideoPreviewActivity;
import com.video.maker.adapter.MusicAdapter;
import com.video.maker.model.MusicType;
import com.video.maker.util.AdManager;
import com.video.maker.util.KSUtil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicFragment extends Fragment {

    public MusicAdapter adapter;
    private CardView btnMyMusic;

    public ImageView btnMyMusicCheck;

    public MusicFragmentListener listener;
    private RecyclerView recyclerMusic;

    public interface MusicFragmentListener {
        void onMyMusic();

        void onTypeMusic(int i);
    }

    public void setMusicFragmentListener(MusicFragmentListener musicFragmentListener) {
        this.listener = musicFragmentListener;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_movie_music, viewGroup, false);
        loadRewardAd(getActivity());
        this.btnMyMusic = (CardView) inflate.findViewById(R.id.btnMyMusic);
        this.btnMyMusicCheck = (ImageView) inflate.findViewById(R.id.imgUnChecked);
        this.recyclerMusic = (RecyclerView) inflate.findViewById(R.id.recyclerMusic);
        setRecyclerMusic();

        this.btnMyMusic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (MusicFragment.this.listener != null) {
                    MusicFragment.this.listener.onMyMusic();
                    MusicFragment.this.adapter.setRowSelected(-1);
                    MusicFragment.this.adapter.notifyDataSetChanged();
                    MusicFragment.this.btnMyMusicCheck.setImageResource(R.drawable.ic_music_check_shape);
                    return;
                }
                Toast.makeText(MusicFragment.this.getActivity(), MusicFragment.this.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
            }
        });


        return inflate;
    }

    private void setRecyclerMusic() {
        this.recyclerMusic.setHasFixedSize(true);
        this.recyclerMusic.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MusicType(R.drawable.love, "LOVE"));
        arrayList.add(new MusicType(R.drawable.friendship, "FRIEND"));
        arrayList.add(new MusicType(R.drawable.christmas, "CHRISTMAS"));
        arrayList.add(new MusicType(R.drawable.positive, "POSITIVE"));
        arrayList.add(new MusicType(R.drawable.movie, "MOVIE"));
        arrayList.add(new MusicType(R.drawable.beach, "BEACH"));
        arrayList.add(new MusicType(R.drawable.summer, "SUMMER"));
        arrayList.add(new MusicType(R.drawable.travel, "TRAVEL"));
        arrayList.add(new MusicType(R.drawable.happy, "HAPPY"));
        MusicAdapter musicAdapter = new MusicAdapter(arrayList, getActivity(), new MusicAdapter.MusicAdapterListener() {
            public void onMusicSelected(int i) {

                if (i + 1 >= 4 && !KSUtil.Musicposs.contains(i)) {
                    showPopupDialog(getActivity(), i);
                    VideoPreviewActivity videoActivity = (VideoPreviewActivity) getActivity();
                    assert videoActivity != null;
                    videoActivity.onPauseVideo();
                } else {

                    if (MusicFragment.this.listener != null) {
                        MusicFragment.this.listener.onTypeMusic(i);
                        MusicFragment.this.btnMyMusicCheck.setImageResource(0);
                        return;
                    }
                    Toast.makeText(MusicFragment.this.getActivity(), MusicFragment.this.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.adapter = musicAdapter;
        this.recyclerMusic.setAdapter(musicAdapter);
    }

    public void showPopupDialog(Activity activity, int position) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = (activity).getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogView.findViewById(R.id.d_purchase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRewardedAd(activity, position);
                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.d_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }
    MaxRewardedAd rewardedAd;
    public static RewardedAd rewardedAdadmob;
    static boolean isReward = false;
    int retryAttempt;

    public void createRewardedAd(Activity activity, int position) {
//        AdManager.Ad_Popup(activity);
        if (AdManager.REMOTE_AD.equals("f")) {
            Log.e("onAdLoaded: ", "hiiiiiiiiiiiiiiii" + getResources().getString(R.string.Max_reword));
            rewardedAd = MaxRewardedAd.getInstance(getResources().getString(R.string.Max_reword), activity);
            rewardedAd.setListener(new MaxRewardedAdListener() {
                @Override
                public void onUserRewarded(MaxAd maxAd, MaxReward maxReward) {
                    MusicReword(position);
//                    AdManager.ProgressDialog.dismiss();
                }

                @Override
                public void onRewardedVideoStarted(MaxAd maxAd) {

                }

                @Override
                public void onRewardedVideoCompleted(MaxAd maxAd) {

                }


                @Override
                public void onAdLoaded(MaxAd maxAd) {
                    Log.e("onAdLoaded: ", "hiiiiiii");
                    retryAttempt = 0;
                    if (rewardedAd.isReady()) {
                        rewardedAd.showAd();
                    }
                    else
                    {
                        rewardedAd.loadAd();
                    }
                }

                @Override
                public void onAdDisplayed(MaxAd maxAd) {
//                    AdManager.ProgressDialog.dismiss();
                }

                @Override
                public void onAdHidden(MaxAd maxAd) {

                }

                @Override
                public void onAdClicked(MaxAd maxAd) {

                }

                @Override
                public void onAdLoadFailed(String s, MaxError maxError) {
                    Log.e("onAdLoaded: ", "ffffff");
                    retryAttempt++;
                    long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));
//                    AdManager.ProgressDialog.dismiss();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rewardedAd.loadAd();
                        }
                    }, delayMillis);
                }

                @Override
                public void onAdDisplayFailed(MaxAd maxAd, MaxError maxError) {
                    Log.e("onAdLoaded: ", "ffffff2");
                    rewardedAd.loadAd();
//                    AdManager.ProgressDialog.dismiss();
                }
            });

            rewardedAd.loadAd();

        } else if (AdManager.REMOTE_AD.equals("a")) {
            new Handler().postDelayed(() -> {
                showRewardAd(activity, position);
            },3000);
        }


    }

    public  void loadRewardAd(Activity context) {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, context.getString(R.string.admob_reward_ad_id),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        rewardedAdadmob = null;
//                        AdManager.ProgressDialog.dismiss();
                        loadRewardAd(context);
                        Log.e("reword: ", "fiiii");
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAdadmob = ad;
                        Log.e("reword: ", "dddddd");
                    }
                });
    }

    public  void showRewardAd(Activity activity,int position) {
//        AdManager.ProgressDialog.dismiss();
        loadRewardAd(activity);
        if (rewardedAdadmob != null) {
            rewardedAdadmob.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    isReward = true;
                }
            });

            rewardedAdadmob.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    if (isReward) {
                            isReward = false;
                            MusicReword(position);

                    }
                }
            });
        }
    }

    public void MusicReword(int position) {

        VideoPreviewActivity videoActivity = (VideoPreviewActivity) getActivity();
        assert videoActivity != null;
        videoActivity.onResumeVideo();
        KSUtil.Musicposs.add(position);
        if (MusicFragment.this.listener != null) {
            MusicFragment.this.listener.onTypeMusic(position);
            MusicFragment.this.btnMyMusicCheck.setImageResource(0);
        }
        Toast.makeText(MusicFragment.this.getActivity(), MusicFragment.this.getString(R.string.try_again), Toast.LENGTH_SHORT).show();

        adapter.notifyDataSetChanged();
    }


}

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.video.maker.util.AdManager;

import java.util.concurrent.TimeUnit;

public class RatioFragment extends Fragment implements View.OnClickListener {
    private FrameLayout border1;
    private FrameLayout border2;
    private FrameLayout border3;
    private FrameLayout border4;
    private FrameLayout border5;
    private RatioFragmentListener listener;

    ImageView premium,premium1,premium2;

    public interface RatioFragmentListener {
        void onRatio(int i);
    }

    public void RatioFragmentListener(RatioFragmentListener ratioFragmentListener) {
        this.listener = ratioFragmentListener;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_movie_ratio, viewGroup, false);
        inflate.findViewById(R.id.btn_square).setOnClickListener(this);
        inflate.findViewById(R.id.btn34).setOnClickListener(this);
        inflate.findViewById(R.id.btn43).setOnClickListener(this);
        inflate.findViewById(R.id.btn169).setOnClickListener(this);
        inflate.findViewById(R.id.btn916).setOnClickListener(this);
        this.border1 = (FrameLayout) inflate.findViewById(R.id.border_1);
        this.border2 = (FrameLayout) inflate.findViewById(R.id.border_2);
        this.border3 = (FrameLayout) inflate.findViewById(R.id.border_3);
        this.border4 = (FrameLayout) inflate.findViewById(R.id.border_4);
        this.border5 = (FrameLayout) inflate.findViewById(R.id.border_5);

        this.premium = inflate.findViewById(R.id.premium);
        this.premium1 = inflate.findViewById(R.id.premium1);
        this.premium2 = inflate.findViewById(R.id.premium2);


        loadRewardAd(getActivity());
        setBorder(0);
        return inflate;
    }

    public void onClick(View view) {
        if (this.listener != null) {
            int id = view.getId();
            if (id != R.id.btn_square) {
                switch (id) {
                    case R.id.btn169:
                        if (premium.getVisibility() == View.GONE) {
                            this.listener.onRatio(169);
                            setBorder(2);

                        }
                        else  {
                            Log.e("onClick: ", "1111");
                            showPopupDialog(getActivity(),169);
                            setBorder(0);

                        }
                        return;

                    case R.id.btn34:
                        if (premium1.getVisibility() == View.GONE ) {
                            this.listener.onRatio(34);
                            setBorder(3);

                        }
                        else  {
                            Log.e("onClick: ", "2222");
                            showPopupDialog(getActivity(),34);
                            setBorder(0);
                          }
                        return;

                    case R.id.btn43:
                        if (premium2.getVisibility() == View.GONE) {
                            this.listener.onRatio(43);
                            setBorder(4);


                        } else  {
                            Log.e("onClick: ", "3333");
                            showPopupDialog(getActivity(),43);
                            setBorder(0);
                        }
                        return;

                    case R.id.btn916:
                            this.listener.onRatio(916);
                            setBorder(1);
                            return;
                    default:
                        return;
                }
            } else {
                this.listener.onRatio(11);
                setBorder(0);
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }
    }

    private void setBorder(int i) {
        FrameLayout[] frameLayoutArr = {this.border1, this.border2, this.border3, this.border4, this.border5};
        for (int i2 = 0; i2 < 5; i2++) {
            if (i2 == i) {
                frameLayoutArr[i2].setVisibility(View.VISIBLE);
            } else {
                frameLayoutArr[i2].setVisibility(View.GONE);
            }
        }
    }

    public  void showPopupDialog(Activity activity, int position)
    {
        VideoPreviewActivity videoActivity = (VideoPreviewActivity) getActivity();
        assert videoActivity != null;
        videoActivity.onPauseVideo();

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = (activity).getLayoutInflater();

        View dialogView= inflater.inflate(R.layout.dialog_layout, null);
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
    int retryAttempt;

    public RewardedAd rewardedAdadmob;

    static boolean isReward = false;

    public void createRewardedAd(Activity activity, int position)
    {

//        AdManager.Ad_Popup(activity);
        if (AdManager.REMOTE_AD.equals("f")) {
            Log.e("onAdLoaded: ", "hiiiiiiiiiiiiiiii" + getResources().getString(R.string.Max_reword));
            rewardedAd = MaxRewardedAd.getInstance(getResources().getString(R.string.Max_reword), activity);
            rewardedAd.setListener(new MaxRewardedAdListener() {
                @Override
                public void onUserRewarded(MaxAd maxAd, MaxReward maxReward) {
                    VideoPreviewActivity videoActivity = (VideoPreviewActivity) getActivity();
                    assert videoActivity != null;
                    videoActivity.onResumeVideo();
                    if (position == 169) {
                        premium.setVisibility(View.GONE);
                        listener.onRatio(169);
                        setBorder(2);

                    } else if (position == 34) {
                        premium1.setVisibility(View.GONE);
                        listener.onRatio(34);
                        setBorder(3);
                    } else if (position == 43) {
                        premium2.setVisibility(View.GONE);
                        listener.onRatio(43);
                        setBorder(4);
                    }
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
//                    AdManager.ProgressDialog.dismiss();
                    long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

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
//            new Handler().postDelayed(() -> {
                showRewardAd(activity, position);
//            },3000);
        }


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

                    VideoPreviewActivity videoActivity = (VideoPreviewActivity) getActivity();
                    assert videoActivity != null;
                    videoActivity.onResumeVideo();

                    if (isReward) {
                        isReward = false;

                        if (position == 169) {
                            premium.setVisibility(View.GONE);
                            listener.onRatio(169);
                            setBorder(2);

                        } else if (position == 34) {
                            premium1.setVisibility(View.GONE);
                            listener.onRatio(34);
                            setBorder(3);
                        } else if (position == 43) {
                            premium2.setVisibility(View.GONE);
                            listener.onRatio(43);
                            setBorder(4);
                        }
                    }
                }
            });
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
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAdadmob = ad;
                    }
                });
    }




}

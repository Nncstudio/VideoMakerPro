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
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.render.video.PhotoMovieFactory;
import com.render.video.PhotoMoviePlayer;
import com.video.maker.R;
import com.video.maker.activities.VideoPreviewActivity;
import com.video.maker.adapter.TransitionsAdapter;
import com.video.maker.model.TransferItem;
import com.video.maker.util.AdManager;
import com.video.maker.util.KSUtil;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TransitionFragment extends Fragment {
    TransitionsAdapter adapter;
    TransferFragmentListener mListener;


    RecyclerView recyclerTransfer;


    public interface TransferFragmentListener {
        void onTransfer(TransferItem transferItem);
    }

    public void setTransferFragmentListener(TransferFragmentListener transferFragmentListener) {
        this.mListener = transferFragmentListener;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_movie_transition, viewGroup, false);
        this.loadRewardAd(getActivity());
        this.recyclerTransfer = (RecyclerView) inflate.findViewById(R.id.recyclerTransfer);
        setRecyclerTransfer();

        return inflate;
    }

    private void setRecyclerTransfer() {
        this.recyclerTransfer.setHasFixedSize(true);
        this.recyclerTransfer.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        ArrayList arrayList = new ArrayList();

        arrayList.add(new TransferItem(R.drawable.gradient_transfer, getString(R.string.str_gradient), PhotoMovieFactory.PhotoMovieType.GRADIENT));
        arrayList.add(new TransferItem(R.drawable.window_transfer, getString(R.string.str_window), PhotoMovieFactory.PhotoMovieType.WINDOW));
        arrayList.add(new TransferItem(R.drawable.random_transfer, getString(R.string.str_random), PhotoMovieFactory.PhotoMovieType.RANDOM));
        arrayList.add(new TransferItem(R.drawable.tranlastion_transfer, getString(R.string.str_tranlation), PhotoMovieFactory.PhotoMovieType.SCALE_TRANS));
        arrayList.add(new TransferItem(R.drawable.leftright_transfer, getString(R.string.str_leftright), PhotoMovieFactory.PhotoMovieType.HORIZONTAL_TRANS));
        arrayList.add(new TransferItem(R.drawable.up_down_transfer, getString(R.string.str_updown), PhotoMovieFactory.PhotoMovieType.VERTICAL_TRANS));
        arrayList.add(new TransferItem(R.drawable.thaw_transfer, getString(R.string.str_thaw), PhotoMovieFactory.PhotoMovieType.THAW));
        arrayList.add(new TransferItem(R.drawable.scale_transfer, getString(R.string.str_scale), PhotoMovieFactory.PhotoMovieType.SCALE));
        TransitionsAdapter transferAdapter = new TransitionsAdapter(arrayList, getActivity(), new TransitionsAdapter.TransferAdapterListener() {
            public void onTransferSelected(TransferItem transferItem,int position) {

                if (position + 1 >= 3&& !KSUtil.Transitionposs.contains(position))
                {
                    showPopupDialog(getActivity(),position,transferItem);
                    VideoPreviewActivity videoActivity = (VideoPreviewActivity) getActivity();
                    assert videoActivity != null;
                    videoActivity.onPauseVideo();
                }
                else {
                    if (TransitionFragment.this.mListener != null) {
                        TransitionFragment.this.mListener.onTransfer(transferItem);
                    } else {
                        Toast.makeText(TransitionFragment.this.getActivity(), TransitionFragment.this.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        this.adapter = transferAdapter;
        this.recyclerTransfer.setAdapter(transferAdapter);
    }


    public  void showPopupDialog(Activity activity, int position, TransferItem transferItem)
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = (activity).getLayoutInflater();

        View dialogView= inflater.inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogView.findViewById(R.id.d_purchase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRewardedAd(activity, position,transferItem);
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



    static boolean isReward = false;
    int retryAttempt;

    public void createRewardedAd(Activity activity, int position, TransferItem transferItem) {
//        AdManager.Ad_Popup(activity);
        if (AdManager.REMOTE_AD.equals("f")) {
            Log.e("onAdLoaded: ", "hiiiiiiiiiiiiiiii" + getResources().getString(R.string.Max_reword));
            rewardedAd = MaxRewardedAd.getInstance(getResources().getString(R.string.Max_reword), activity);
            rewardedAd.setListener(new MaxRewardedAdListener() {
                @Override
                public void onUserRewarded(MaxAd maxAd, MaxReward maxReward) {
                    TransReword(position,transferItem);
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
                showRewardAd(activity, position, transferItem);
            },3000);
        }

    }




    public  void showRewardAd(Activity activity,int position,TransferItem filterItem) {
//        AdManager.ProgressDialog.dismiss();
        Log.e( "loaddd","showw");

        loadRewardAd(activity);

        if (rewardedAdadmob != null) {
            Log.e( "loaddd","showw11");
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
                        TransReword(position,filterItem);
                    }
                }
            });
        }else {
            Log.e( "loaddd","hiii");
            loadRewardAd(activity);
        }
    }

    public RewardedAd rewardedAdadmob;

    public  void loadRewardAd(Activity context) {
        Log.e( "loadRewardAd: ","1111" );
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, context.getString(R.string.admob_reward_ad_id),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        rewardedAdadmob = null;
//                        AdManager.ProgressDialog.dismiss();
                        Log.e( "loadRewardAd: ","ffff" );
                        loadRewardAd(context);
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAdadmob = ad;
                        Log.e( "loadRewardAd: ","lllll" );
                    }
                });
    }



    public  void TransReword(int position, TransferItem transferItem)
    {
        VideoPreviewActivity videoActivity = (VideoPreviewActivity) getActivity();
        assert videoActivity != null;
        videoActivity.onResumeVideo();
        KSUtil.Transitionposs.add(position);
        if (TransitionFragment.this.mListener != null) {
            TransitionFragment.this.mListener.onTransfer(transferItem);
        } else {
            Toast.makeText(TransitionFragment.this.getActivity(), TransitionFragment.this.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }




}

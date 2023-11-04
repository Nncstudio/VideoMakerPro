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
import com.video.maker.R;
import com.video.maker.activities.VideoPreviewActivity;
import com.video.maker.adapter.FilterAdapter;
import com.video.maker.model.FilterItem;
import com.video.maker.model.FilterType;
import com.video.maker.util.AdManager;
import com.video.maker.util.KSUtil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class FilterFragment extends Fragment {
    FilterAdapter adapter;
    FilterFragmentListener mfilterFragmentListener;
    RecyclerView recyclerFilter;

    public interface FilterFragmentListener {
        void onFilter(FilterItem filterItem);
    }

    public void setFilterFragmentListener(FilterFragmentListener filterFragmentListener) {
        this.mfilterFragmentListener = filterFragmentListener;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_movie_filter, viewGroup, false);
        loadRewardAd(getActivity());
        this.recyclerFilter = (RecyclerView) inflate.findViewById(R.id.recyclerFilter);
        setRecyclerFilter();

        return inflate;
    }

    private void setRecyclerFilter() {
        this.recyclerFilter.setHasFixedSize(true);
        this.recyclerFilter.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        ArrayList arrayList = new ArrayList();
        arrayList.add(new FilterItem(R.drawable.filter, "Normal", FilterType.NONE));
        arrayList.add(new FilterItem(R.drawable.filter_gray, "Black White", FilterType.GRAY));
        arrayList.add(new FilterItem(R.drawable.filter_snow, "Snow", FilterType.SNOW));
        arrayList.add(new FilterItem(R.drawable.filter_l1, "Walden", FilterType.LUT1));
        arrayList.add(new FilterItem(R.drawable.filter_l2, "Beauty", FilterType.LUT2));
        arrayList.add(new FilterItem(R.drawable.filter_l3, "Reddest", FilterType.LUT3));
        arrayList.add(new FilterItem(R.drawable.filter_l4, "Vintage", FilterType.LUT4));
        arrayList.add(new FilterItem(R.drawable.filter_l5, "Lasso", FilterType.LUT5));
        arrayList.add(new FilterItem(R.drawable.cameo, "Cameo", FilterType.CAMEO));
        FilterAdapter filterAdapter = new FilterAdapter(arrayList, getActivity(), new FilterAdapter.FilterAdapterListener() {
            @Override
            public void onFilterSelected(FilterItem filterItem, int position) {

                if (position + 1 >= 4 && !KSUtil.Filterposs.contains(position))
                {
                    showPopupDialog(getActivity(),position,filterItem);
                    VideoPreviewActivity videoActivity = (VideoPreviewActivity) getActivity();
                    assert videoActivity != null;
                    videoActivity.onPauseVideo();
                }
                else
                {
                    if (FilterFragment.this.mfilterFragmentListener != null) {
                        FilterFragment.this.mfilterFragmentListener.onFilter(filterItem);
                    } else {
                        Toast.makeText(FilterFragment.this.getActivity(), FilterFragment.this.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    }
                }

            }



        });
        this.adapter = filterAdapter;
        this.recyclerFilter.setAdapter(filterAdapter);
    }



    public  void showPopupDialog(Activity activity,int position,FilterItem filterItem)
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
                createRewardedAd(activity, position,filterItem);
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

    public RewardedAd rewardedAdadmob;

    static boolean isReward = false;
    int retryAttempt;

    public void createRewardedAd(Activity activity, int position,FilterItem filterItem) {
//        AdManager.Ad_Popup(activity);
        if (AdManager.REMOTE_AD.equals("f")) {
            Log.e("onAdLoaded: ", "hiiiiiiiiiiiiiiii" + getResources().getString(R.string.Max_reword));
            rewardedAd = MaxRewardedAd.getInstance(getResources().getString(R.string.Max_reword), activity);
            rewardedAd.setListener(new MaxRewardedAdListener() {
                @Override
                public void onUserRewarded(MaxAd maxAd, MaxReward maxReward) {
                    FilterReword(position,filterItem);
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
//            new Handler().postDelayed(() -> {
                showRewardAd(activity, position, filterItem);
//            },3000);
        }


    }



    public  void showRewardAd(Activity activity,int position,FilterItem filterItem) {
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
                        FilterReword(position,filterItem);
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



    public  void FilterReword(int position, FilterItem filterItem)
        {
            VideoPreviewActivity videoActivity = (VideoPreviewActivity) getActivity();
            assert videoActivity != null;
            videoActivity.onResumeVideo();
            KSUtil.Filterposs.add(position);
            if (FilterFragment.this.mfilterFragmentListener != null) {
                FilterFragment.this.mfilterFragmentListener.onFilter(filterItem);
            } else {
                Toast.makeText(FilterFragment.this.getActivity(), FilterFragment.this.getString(R.string.try_again), Toast.LENGTH_SHORT).show();
            }
            adapter.notifyDataSetChanged();
        }

}

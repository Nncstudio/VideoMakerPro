package com.video.maker.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.applovin.sdk.AppLovinSdk;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.video.maker.R;


public class AdManager {
    public static int adCounter = 1;
    public static int adDisplayCounter = 10;

//    public static AlertDialog ProgressDialog;

    public static String REMOTE_AD = "a";

    static void startActivity(Activity context, Intent intent, int requestCode) {
        if (intent != null) {
            context.startActivityForResult(intent, requestCode);
        }
    }


//    public static void Ad_Popup(Context context) {
//        ProgressDialog = null;
//        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
//        View view = layoutInflaterAndroid.inflate(R.layout.addialog_loader, null);
//        AlertDialog.Builder alert = null;
//        alert = new AlertDialog.Builder(context);
//        alert.setView(view);
//
//        ProgressDialog = alert.create();
//
//        ProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        ProgressDialog.setCancelable(false);
//
//        ProgressDialog.show();
//
//    }


    static MaxAdView maxAdView;

    public static void initAD(Activity activity) {

        if ( REMOTE_AD.equals("a")) {

            MobileAds.initialize(activity, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
        }else if (AdManager.REMOTE_AD.equals("f")) {
            AppLovinSdk.getInstance(activity).setMediationProvider("max");
            AppLovinSdk.initializeSdk(activity, configuration -> {
            });
        }
    }

    public static void BannerAd(Activity activity, LinearLayout linearLayout, int color) {

        if ( REMOTE_AD.equals("a")) {
            final AdView mAdView = new AdView(activity);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(activity.getString(R.string.admob_banner_id));
            AdRequest adore = new AdRequest.Builder().build();
            mAdView.loadAd(adore);
            linearLayout.addView(mAdView);


            mAdView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    linearLayout.setVisibility(View.VISIBLE);
                    super.onAdLoaded();

                    Log.e("ddddd", "dddd");
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    linearLayout.setVisibility(View.GONE);
                    Log.e("ddddd1", "dddd");

                }

                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    linearLayout.setVisibility(View.GONE);
                    Log.e("ddddd2", "dddd" + loadAdError.getMessage());

                }
            });
        } else if (REMOTE_AD.equals("f")) {

            maxAdView = new MaxAdView(activity.getResources().getString(R.string.max_banner), activity);

            // Stretch to the width of the screen for banners to be fully functional
            int width = ViewGroup.LayoutParams.MATCH_PARENT;

            // Banner height on phones and tablets is 50 and 90, respectively
            int heightPx = activity.getResources().getDimensionPixelSize(R.dimen.banner_height);

            maxAdView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));

            // Set background or background color for banners to be fully functional
            maxAdView.setBackgroundColor(color);

            if (isNetworkConnected(activity)) {
                linearLayout.addView(maxAdView);

                // Load the banner
                if (maxAdView != null) {
                    maxAdView.loadAd();
                }
            }
        }

    }

    static Intent maxIntent;
    static int maxRequstCode;
    static MaxInterstitialAd maxInterstitialAd;

    public static void LoadInterstitalAd(Activity activity) {

            loadInterAd(activity);

            maxInterstitialAd = new MaxInterstitialAd(activity.getResources().getString(R.string.max_interstitial), activity);
            maxInterstitialAd.setListener(new MaxAdListener() {
                @Override
                public void onAdLoaded(MaxAd ad) {

                }

                @Override
                public void onAdDisplayed(MaxAd ad) {

                }

                @Override
                public void onAdHidden(MaxAd ad) {
                    maxInterstitialAd.loadAd();
                    startActivity(activity, maxIntent, maxRequstCode);
                }

                @Override
                public void onAdClicked(MaxAd ad) {

                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    if (isNetworkConnected(activity)) {
                        maxInterstitialAd.loadAd();
                    }
                    startActivity(activity, maxIntent, maxRequstCode);
//                    ProgressDialog.dismiss();
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                    if (isNetworkConnected(activity)) {
                        maxInterstitialAd.loadAd();
                    }
                    startActivity(activity, maxIntent, maxRequstCode);
//                    ProgressDialog.dismiss();
                }
            });

            if (isNetworkConnected(activity)) {
                // Load the first ad
                maxInterstitialAd.loadAd();
            }

    }

    static InterstitialAd mInterstitialAd;
    public static void loadInterAd(Context context) {

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, context.getString(R.string.admob_inter_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;
//                ProgressDialog.dismiss();
                startActivity((Activity) context, maxIntent, maxRequstCode);
            }
        });


    }
    public static void showInterAd(final Activity context,final Intent intent, final int requestCode) {

            if (mInterstitialAd != null) {

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        loadInterAd(context);
                        startActivity(context, maxIntent, maxRequstCode);
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
//                        ProgressDialog.dismiss();
                    }


                    @Override
                    public void onAdShowedFullScreenContent() {
                        mInterstitialAd = null;

                    }
                });

                maxIntent = intent;
                maxRequstCode = requestCode;

                if (isNetworkConnected(context)) {
                    if (adCounter == adDisplayCounter && mInterstitialAd != null ) {
                        adCounter = 1;
//                        Ad_Popup(context);

                            mInterstitialAd.show((Activity) context);
//                            ProgressDialog.dismiss();

                    } else {
                        if (adCounter == adDisplayCounter) {
                            adCounter = 1;
                        }
                        startActivity(context, intent, requestCode);
                    }
                }else {
                    startActivity(context, intent, requestCode);
                }

            }
    }

    public static void showMaxInterstitial(final Activity context, final Intent intent, final int requestCode) {
        if (REMOTE_AD.equals("a")) {
            showInterAd(context,intent,requestCode);
        } else if (REMOTE_AD.equals("f")) {

            maxIntent = intent;
            maxRequstCode = requestCode;

            if (isNetworkConnected(context)) {
                if (adCounter == adDisplayCounter && maxInterstitialAd != null && maxInterstitialAd.isReady()) {
                    adCounter = 1;
//                    Ad_Popup(context);
//                    new Handler().postDelayed(() -> {
                        maxInterstitialAd.showAd();
//                        ProgressDialog.dismiss();
//                    },3000);
                } else {
                    if (adCounter == adDisplayCounter) {
                        adCounter = 1;
                    }
                    startActivity(context, intent, requestCode);
                }
            }else {
                startActivity(context, intent, requestCode);
            }
        }
    }


    static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    static MaxAd nativeAd;

    public static void createNativeAdMAX(Activity context, RelativeLayout nativeAdContainer) {

        if (REMOTE_AD.equals("a")) {
            AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(R.string.admob_native_id))
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {

                            NativeAdView adView = (NativeAdView) context.getLayoutInflater()
                                    .inflate(R.layout.ad_lay, null);
                            // This method sets the text, images and the native ad, etc into the ad
                            // view.
                            populateNativeAdView(nativeAd, adView);
                            nativeAdContainer.removeAllViews();
                            nativeAdContainer.addView(adView);
                        }
                    });

            VideoOptions videoOptions =
                    new VideoOptions.Builder().setStartMuted(true).build();

            NativeAdOptions adOptions =
                    new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(
                            new AdListener() {
                                @Override
                                public void onAdFailedToLoad(LoadAdError loadAdError) {
                                }
                            })
                    .build();

            adLoader.loadAds(new AdRequest.Builder().build(),10);

        }else if (REMOTE_AD.equals("f")) {

            nativeAd = null;
            MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(context.getResources().getString(R.string.max_native), context);
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(final MaxNativeAdView nativeAdView, final MaxAd ad) {
                    // Clean up any pre-existing native ad to prevent memory leaks.
                    if (nativeAd != null) {
                        nativeAdLoader.destroy(nativeAd);
                    }

                    // Save ad for cleanup.
                    nativeAd = ad;

                    // Add ad view to view.
                    nativeAdContainer.removeAllViews();
                    nativeAdContainer.addView(nativeAdView);
                }

                @Override
                public void onNativeAdLoadFailed(final String adUnitId, final MaxError error) {
                    // We recommend retrying with exponentially higher delays up to a maximum delay
                }

                @Override
                public void onNativeAdClicked(final MaxAd ad) {
                    // Optional click callback
                }
            });

            nativeAdLoader.loadAd();
        }
    }


    public static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());


        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);

    }

}

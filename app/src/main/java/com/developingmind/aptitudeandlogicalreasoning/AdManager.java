package com.developingmind.aptitudeandlogicalreasoning;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdManager {
    Context mContext;
    Boolean isInitialized = false;

    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    private AdRequest adRequest;

    // To be removed after purchase
    Boolean isPurchased = false;

    public AdManager(@NonNull Context context){
        mContext = context;
        MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                if(initializationStatus.getAdapterStatusMap().get("com.google.android.gms.ads.MobileAds").getInitializationState() == AdapterStatus.State.READY){
                    isInitialized = true;
                }

            }
        });

        createAdRequest();
    }

    public void loadRewardedAd(){
        if (!isPurchased) {
            RewardedAd.load(mContext, mContext.getString(R.string.videoAd_ID), adRequest,
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            mRewardedAd = null;
                            super.onAdFailedToLoad(loadAdError);
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            mRewardedAd = rewardedAd;
                            super.onAdLoaded(rewardedAd);
                        }
                    });
        }
    }

    public Boolean showRewardedAd(Activity activity){
        if (mRewardedAd!=null && !isPurchased){
            mRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    int reward = rewardItem.getAmount();
                }
            });
            return true;
        }else{
            return false;
        }
    }

    public void loadInterstitialAd(){
        if (!isPurchased) {
            InterstitialAd.load(mContext, mContext.getString(R.string.interstitialAd_ID), adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                        super.onAdFailedToLoad(loadAdError);
                            mInterstitialAd = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                        super.onAdLoaded(interstitialAd);
                            mInterstitialAd = interstitialAd;
                        }
                    });
        }
    }

    public Boolean showInterstitialAd(Activity activity){
        if (mInterstitialAd!=null && !isPurchased){
            mInterstitialAd.show(activity);
            return true;
        }else{
            return false;
        }
    }

    public void createAdRequest(){
        if(!isPurchased)
            adRequest = new AdRequest.Builder().build();
    }

    public void loadBannerAd(AdView mAdView){
        if(!isPurchased)
            mAdView.loadAd(adRequest);
    }

}

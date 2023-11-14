package com.developingmind.aptitudeandlogicalreasoning;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.io.Serializable;

public class AdManager extends Application{
//    Context mContext;
    Boolean isInitialized = false;

    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    private AdRequest adRequest;

    // To be removed after purchase
    Boolean isPurchased = false;


    @Override
    public void onCreate() {
        MobileAds.initialize(getApplicationContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                if(initializationStatus.getAdapterStatusMap().get("com.google.android.gms.ads.MobileAds").getInitializationState() == AdapterStatus.State.READY){
                    isInitialized = true;
                }
            }
        });
        super.onCreate();
    }

    public void loadRewardedAd(){
        if (!isPurchased) {
            RewardedAd.load(getApplicationContext(), getApplicationContext().getString(R.string.videoAd_ID), adRequest,
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


    public Boolean showRewardedAd(Activity activity, SharedPreferences sharedPreferences, TextView credits){
        if (mRewardedAd!=null && !isPurchased){
            mRewardedAd.show(activity, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    int reward = rewardItem.getAmount();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("share_count",sharedPreferences.getInt("share_count",0)+reward);
                    editor.apply();

                    String share = getResources().getString(R.string.share_text);
                    share = share.concat(String.valueOf(sharedPreferences.getInt("share_count",0)));
                    credits.setText(share);

                    loadRewardedAd();
                }
            });
            return true;
        }else{
            return false;
        }
    }

    public void loadInterstitialAd(){
        if (!isPurchased) {
            InterstitialAd.load(getApplicationContext(), getApplicationContext().getString(R.string.interstitialAd_ID), adRequest,
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

    public InterstitialAd getmInterstitialAd(){
        return mInterstitialAd;
    }

    public Boolean showInterstitialAd(Activity activity){
        if (mInterstitialAd!=null && !isPurchased){
            mInterstitialAd.show(activity);
            loadInterstitialAd();
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

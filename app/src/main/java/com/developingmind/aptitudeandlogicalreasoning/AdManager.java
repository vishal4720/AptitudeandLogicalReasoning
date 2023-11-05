package com.developingmind.aptitudeandlogicalreasoning;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class AdManager {
    Context mContext;
    Boolean isInitialized = false;
    AdRequest adRequest;

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

    public void createAdRequest(){
        adRequest = new AdRequest.Builder().build();
    }

    public void loadBannerAd(AdView mAdView){
        mAdView.loadAd(adRequest);
    }

}

package com.developingmind.aptitudeandlogicalreasoning.purchase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchaseHistoryParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.developingmind.aptitudeandlogicalreasoning.AdManager;
import com.developingmind.aptitudeandlogicalreasoning.Constants;
import com.developingmind.aptitudeandlogicalreasoning.DialogMaker;
import com.developingmind.aptitudeandlogicalreasoning.HomeActivity;
import com.developingmind.aptitudeandlogicalreasoning.R;
import com.developingmind.aptitudeandlogicalreasoning.databinding.ActivitySubscriptionBinding;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Subscription {

    private BillingClient billingClient;
    boolean isSuccess = false;
    private ProductDetails productDetails;
    ActivitySubscriptionBinding binding;
    private Context context;
    private Activity activity;
    DialogMaker loadingDialog,purchaseDialog;

    public Subscription(@NonNull Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void create() {

        loadingDialog = new DialogMaker(context);

        billingClient = BillingClient.newBuilder(context)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    ((HomeActivity)context).checkPastPurchase();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    public BillingClient getBillingClient(){
        return billingClient;
    }

    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }else {
                AdManager adManager = (AdManager) context.getApplicationContext();
                adManager.setIsPurchased(true);
            }
        }
    }

    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
            if (billingResult.getResponseCode()== BillingClient.BillingResponseCode.OK){
                Log.d("Acknowledge Purchase",billingResult.toString());
                AdManager adManager = (AdManager) context.getApplicationContext();
                adManager.setIsPurchased(true);
                ((HomeActivity)context).purchaseSuccessful();
            }
        }
    };

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                Toast.makeText(context, "Purchase Canceled", Toast.LENGTH_SHORT).show();
            } else {
                // Handle any other error codes.
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){
                    Toast.makeText(context, "Already Purchased !!", Toast.LENGTH_SHORT).show();
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.NETWORK_ERROR) {
                    Toast.makeText(context, "Check your network and try again !", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(context, "Something went wrong try again after some time !", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    public void getSubscription(){
        loadingDialog.getDialog().show();
        billingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingServiceDisconnected() {
                Log.d("Billing Disconnected","Billing Disconnected");
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                Log.d("Billing Result",billingResult.getDebugMessage());
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            QueryProductDetailsParams queryProductDetailsParams =
                                    QueryProductDetailsParams.newBuilder()
                                            .setProductList(
                                                    ImmutableList.of(
                                                            QueryProductDetailsParams.Product.newBuilder()
                                                                    .setProductId("remove_adss")
                                                                    .setProductType(BillingClient.ProductType.INAPP)
                                                                    .build()))
                                            .build();
                            billingClient.queryProductDetailsAsync(queryProductDetailsParams, new ProductDetailsResponseListener() {
                                @Override
                                public void onProductDetailsResponse(@NonNull BillingResult billingResult, @NonNull List<ProductDetails> list) {
                                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                        for (ProductDetails product:
                                                list) {
                                            Log.d("Billing Products",product.toString());

                                            productDetails = product;
                                            loadingDialog.getDialog().dismiss();
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    purchaseDialog = new DialogMaker(context,productDetails.getTitle(),productDetails.getDescription(),productDetails.getOneTimePurchaseOfferDetails().getFormattedPrice());
                                                    purchaseDialog.getDialog().show();
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(context, "Google Play not Installed", Toast.LENGTH_SHORT).show();
                    loadingDialog.getDialog().dismiss();
                }
            }
        });
    }

    public void startBilling(){
        ImmutableList productDetailsParamsList =
                ImmutableList.of(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                .setProductDetails(productDetails)
                                // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                                // for a list of offers that are available to the user
//                                .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                .build()
                );

        Log.d("ProductDetails",productDetails.toString());

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();


        // Launch the billing flow
        BillingResult billingResult1 = billingClient.launchBillingFlow( activity, billingFlowParams);
        if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK){
            Log.d("Billing Success",billingResult1.toString());
        }else {
            Log.d("Error","Error");
        }
    }
}
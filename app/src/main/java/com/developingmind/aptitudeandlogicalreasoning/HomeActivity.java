package com.developingmind.aptitudeandlogicalreasoning;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.developingmind.aptitudeandlogicalreasoning.home.AptitudeFragment;
import com.developingmind.aptitudeandlogicalreasoning.home.LogicalFragment;
import com.developingmind.aptitudeandlogicalreasoning.leaderboard.LeaderboardFragment;
import com.developingmind.aptitudeandlogicalreasoning.login.LoginActivity;
import com.developingmind.aptitudeandlogicalreasoning.login.SignUpActivity;
import com.developingmind.aptitudeandlogicalreasoning.notification.NotificationActivity;
import com.developingmind.aptitudeandlogicalreasoning.profile.Gender;
import com.developingmind.aptitudeandlogicalreasoning.profile.ProfileEnum;
import com.developingmind.aptitudeandlogicalreasoning.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.common.collect.ImmutableList;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    TextView headerTitle;
    ShapeableImageView headerIcon;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    Uri profileUrl;

    Dialog logOutDialog;

    private BillingClient billingClient;


    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
//                    handlePurchase(purchase);
                    Log.d("Billing Success",purchase.toString());
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                Log.d("Billing Cancel","User Cancelled");
            } else {
                // Handle any other error codes.
                Log.d("Billing Error",billingResult.getDebugMessage());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

         firebaseAuth = FirebaseAuth.getInstance();

         if(firebaseAuth.getCurrentUser() == null){
             startActivity(new Intent(this,LoginActivity.class));
             finish();
         }

        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        // If the permission is not granted, request it.
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
//        getBitmapFromVectorDrawable(this,R.drawable.ic_train);


        // Billing Start

        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        // Billing End

        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View headerView = navigationView.getHeaderView(0);
        headerTitle = headerView.findViewById(R.id.header_title);
        headerIcon = headerView.findViewById(R.id.header_icon);

        navigationView.bringToFront();
        navigationView.setItemIconTintList(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_aptitude);
        navigationView.setNavigationItemSelectedListener(this);

        setVersionCode();
        setHeaderTitle();
        getMaintenanceStatus(this);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new AptitudeFragment()).commit();
        }

    }

    private void startBilling(){
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
                                                                    .setProductId("remove_ads")
                                                                    .setProductType(BillingClient.ProductType.SUBS)
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
                                        }
                                    }
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
//                                        throw new RuntimeException(e);
                                    }
                                    ImmutableList productDetailsParamsList =
                                            ImmutableList.of(
                                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                                            // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                                                            .setProductDetails(productDetails)
                                                            // to get an offer token, call ProductDetails.getSubscriptionOfferDetails()
                                                            // for a list of offers that are available to the user
                                                            .setOfferToken(productDetails.getSubscriptionOfferDetails().get(0).getOfferToken())
                                                            .build()
                                            );

                                    Log.d("ProductDetails",productDetails.toString());

                                    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                            .setProductDetailsParamsList(productDetailsParamsList)
                                            .build();


                                    // Launch the billing flow
                                    BillingResult billingResult1 = billingClient.launchBillingFlow(HomeActivity.this, billingFlowParams);
                                    if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK){
                                        Log.d("Billing Success",billingResult1.toString());
                                    }else {
                                        Log.d("Error","Error");
                                    }
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(HomeActivity.this, "Google Play not Installed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    ProductDetails productDetails;

    public void getMaintenanceStatus(@NonNull Context context){
        firebaseFirestore.collection(DatabaseEnum.system.toString())
                .document(DatabaseEnum.maintenance.toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            MaintenanceModal maintenanceModal = documentSnapshot.toObject(MaintenanceModal.class);
                            Date date = maintenanceModal.getTill().toDate();
                            if(maintenanceModal.getStatus() && maintenanceModal.getTill().toDate().compareTo(Calendar.getInstance().getTime())>0) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                                DialogMaker dialogMaker = new DialogMaker(maintenanceModal.getMessage(), dateFormat.format(date).toString(),context);
                                dialogMaker.getDialog().show();
                            }
                        }
                    }
                });
    }



    private void setVersionCode(){
        String version = "";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setTitle("Version Code : "+version).setEnabled(false);

    }

    public void setHeaderIcon(int drawable) {
        headerIcon.setImageDrawable(getDrawable(drawable));
    }

    public void setHeaderTitle(String name){
        String title = name
                + "\n" + firebaseUser.getEmail().toString();
        headerTitle.setText(title);
    }

    private void setTitle(String s){
        toolbar.setTitle(s);
    }

    private void setHeaderTitle(){
        FirebaseFirestore.getInstance().collection(DatabaseEnum.users.toString())
                .document(firebaseUser.getUid().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful()){
                            headerTitle.setText("Name\nEmail");
                        }
                        DocumentSnapshot documentSnapshot = task.getResult();
                        try {
                            Map<String, Object> map = documentSnapshot.getData();
                            String title = map.get(ProfileEnum.fname.toString()).toString() + " " + map.get(ProfileEnum.lname.toString()).toString()
                                    + "\n" + firebaseUser.getEmail().toString();
                            headerTitle.setText(title);
                            updateProfile(map,headerIcon);

//                            Log.d("Email", title);
                        }catch(Exception e){
                            Log.d("Exceptions",e.getMessage());
                            Toast.makeText(HomeActivity.this, "Please complete Sign Up", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(HomeActivity.this, SignUpActivity.class);
                            intent.putExtra(Constants.isLogin,true);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        headerTitle.setText("Name\nEmail");
                    }
                });
    }

    public void updateProfile(Map<String,Object> map,ShapeableImageView image){
        try{
            profileUrl = Uri.parse(map.get(ProfileEnum.profile.toString()).toString());
            Picasso.get()
                    .load(profileUrl)
                    .into(image);

        }catch (Exception e){
            Log.d("Profile Exc",e.getMessage());
            if (map.get(ProfileEnum.gender.toString()).toString().equals(Gender.Male.toString())) {
                setHeaderIcon(R.drawable.male_avatar);
            } else {
                setHeaderIcon(R.drawable.female_avatar);
            }
        }
    }

    public Uri getProfileStatus(Map<String,Object> map){
        try{
            return Uri.parse(map.get(ProfileEnum.profile.toString()).toString());

        }catch (Exception e){
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int itemId = navigationView.getCheckedItem().getItemId();
        if (itemId == R.id.nav_aptitude) {
            setTitle("Aptitude");
        }else if(itemId == R.id.nav_logical){
            setTitle("Logical Reasoning");
        }else if (itemId == R.id.nav_profile) {
            setTitle("Profile");
        } else if (itemId == R.id.nav_leaderboard) {
            setTitle("Leaderboard");
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.nav_notification){
            startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
            return true;
        } else if (item.getItemId() == R.id.nav_remove_ads) {
            startBilling();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_main_menu,menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            if(navigationView.getCheckedItem().getItemId() != R.id.nav_aptitude && navigationView.getCheckedItem().getItemId() != R.id.nav_logical){
                 navigationView.setCheckedItem(R.id.nav_aptitude);
                setTitle("Aptitude");
                changeFragment(new AptitudeFragment());
            }else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment frag = null; // create a Fragment Object
        int itemId = item.getItemId(); // get selected menu item's id
        // check selected menu item's id and replace a Fragment Accordingly
        if (itemId == R.id.nav_aptitude) {
            setTitle("Aptitude");
            frag = new AptitudeFragment();
        }else if(itemId == R.id.nav_logical){
            setTitle("Logical Reasoning");
            frag = new LogicalFragment();
        }else if (itemId == R.id.nav_profile) {
            setTitle("Profile");
            frag = new ProfileFragment();
        } else if (itemId == R.id.nav_leaderboard) {
            setTitle("Leaderboard");
            frag = new LeaderboardFragment();
        } else if (itemId == R.id.nav_rate) {
            String url = getResources().getString(R.string.play_store_link);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (itemId == R.id.nav_feedback) {
            DialogMaker dialogMaker = new DialogMaker(this,firebaseFirestore,firebaseUser);
            dialogMaker.getDialog().show();
            drawerLayout.closeDrawers();
        } else if (itemId == R.id.nav_privacy) {

        } else if (itemId == R.id.nav_logout) {
            createDialog();
        }
        if (frag != null) {
            changeFragment(frag);
            return true;
        }
        return false;
    }

    private void createDialog(){
        logOutDialog = new Dialog(this);
        logOutDialog.setContentView(R.layout.dialog_quit);
        logOutDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        logOutDialog.setCancelable(true);
        logOutDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.category_bg));
        ((TextView) logOutDialog.findViewById(R.id.quit_title)).setText("LogOut");
        TextView desc = ((TextView) logOutDialog.findViewById(R.id.quit_desc));
        desc.setText("Are you Sure ?");
        desc.setVisibility(View.VISIBLE);
        ((Button) logOutDialog.findViewById(R.id.no)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutDialog.dismiss();
            }
        });
        ((Button) logOutDialog.findViewById(R.id.yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        logOutDialog.show();
    }

    private void changeFragment(Fragment frag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, frag); // replace a Fragment with Frame Layout
        transaction.commit(); // commit the changes
        drawerLayout.closeDrawers(); // close the all open Drawer Views
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public FirebaseFirestore getFirebaseFirestore() {
        return firebaseFirestore;
    }

    public void logOut(){
        firebaseAuth.signOut();
        logOutDialog.dismiss();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public FirebaseAuth getFirebaseAuth(){
        return firebaseAuth;
    }
}
package com.developingmind.aptitudeandlogicalreasoning;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.developingmind.aptitudeandlogicalreasoning.home.AptitudeFragment;
import com.developingmind.aptitudeandlogicalreasoning.home.LogicalFragment;
import com.developingmind.aptitudeandlogicalreasoning.leaderboard.LeaderboardFragment;
import com.developingmind.aptitudeandlogicalreasoning.login.LoginActivity;
import com.developingmind.aptitudeandlogicalreasoning.login.SignUpActivity;
import com.developingmind.aptitudeandlogicalreasoning.notification.NotificationActivity;
import com.developingmind.aptitudeandlogicalreasoning.profile.Gender;
import com.developingmind.aptitudeandlogicalreasoning.profile.ProfileEnum;
import com.developingmind.aptitudeandlogicalreasoning.profile.ProfileFragment;
import com.developingmind.aptitudeandlogicalreasoning.purchase.Subscription;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

//    private BillingClient billingClient;
    Subscription subscription;
    
    AdManager adManager;
    AdView adView;

    String privacyPolicy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

         firebaseAuth = FirebaseAuth.getInstance();

         if(firebaseAuth.getCurrentUser() == null){
             startActivity(new Intent(this,LoginActivity.class));
             finish();
         }

        // Billing Start
        subscription = new Subscription(this,this);

        subscription.create();

        // Billing End

        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        // If the permission is not granted, request it.
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }



        adManager = (AdManager)getApplicationContext();
        adManager.createAdRequest();
        adView = findViewById(R.id.bannerAdView);
        
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

        loadBannerAd();
        loadInterstitialAd();

    }

    public void loadBannerAd(){
        adManager.loadBannerAd(adView);
    }
    public void loadInterstitialAd(){
        adManager.loadInterstitialAd();
    }
    public Boolean showInterstitialAd(){
        return adManager.showInterstitialAd(HomeActivity.this);
    }

    private void getSubscription(){
        subscription.getSubscription();
    }

    public void checkPastPurchase(){
        BillingClient billingClient = subscription.getBillingClient();
        Log.d("Purchase History", "Start");

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {

            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder()
                                    .setProductType(BillingClient.ProductType.INAPP)
                                    .build(),
                            new PurchasesResponseListener() {
                                public void onQueryPurchasesResponse(BillingResult billingResult, List purchases) {
                                    // check billingResult
                                    // process returned purchase list, e.g. display the plans user owns
                                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                        Log.d("Purchase History", String.valueOf(purchases.size()));
                                        if (purchases.size()>0){
                                            Log.d("Purchase History", purchases.get(0).toString());
                                            adManager.setIsPurchased(true);
                                            adView.setVisibility(View.GONE);
                                            purchaseSuccessful();
                                            toolbar.getMenu().findItem(R.id.nav_remove_ads).setVisible(false);
                                            Log.d("Purchase History Menu","Start");
                                            toolbar.invalidateMenu();
                                        }
                                    }
                                }
                            }
                    );
                }
            }
        });
    }

    public void startBilling(){
        subscription.startBilling();
    }

    public void purchaseSuccessful(){
        SharedPreferences purchaseSharedPreference = getSharedPreferences("PurchasePref",MODE_PRIVATE);
        SharedPreferences.Editor editor = purchaseSharedPreference.edit();
        editor.putBoolean("purchase",true);
        editor.apply();
    }

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
                            privacyPolicy = maintenanceModal.getPrivacyPolicy();
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

    private void setPurchaseHeader(){
        if (adManager.isPurchased){
            headerIcon.setStrokeWidth(10);
            headerIcon.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FFD700")));
        }
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
            if(!adManager.isPurchased)
                getSubscription();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (adManager.isPurchased){
            menu.removeItem(R.id.nav_remove_ads);
        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sub_main_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(adManager.isPurchased){
            menu.removeItem(R.id.nav_remove_ads);
            setPurchaseHeader();
        }
        Log.d("Prepare Menu","YES");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            if(navigationView.getCheckedItem().getItemId() != R.id.nav_aptitude && navigationView.getCheckedItem().getItemId() != R.id.nav_logical){
                 navigationView.setCheckedItem(R.id.nav_aptitude);
                setTitle("Aptitude");
                changeFragment(new AptitudeFragment(),"Aptitude");
            }else {
                super.onBackPressed();
            }
        }
    }

    Fragment frag = null;
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String id=null;
        int itemId = item.getItemId(); // get selected menu item's id
        // check selected menu item's id and replace a Fragment Accordingly
        if (itemId == R.id.nav_aptitude) {
            setTitle("Aptitude");
            frag = new AptitudeFragment();
            id="Aptitude";
        }else if(itemId == R.id.nav_logical){
            setTitle("Logical Reasoning");
            frag = new LogicalFragment();
            id="Logical Reasoning";
        }else if (itemId == R.id.nav_profile) {
            setTitle("Profile");
            frag = new ProfileFragment();
            id="Profile";
        } else if (itemId == R.id.nav_leaderboard) {
            setTitle("Leaderboard");
            frag = new LeaderboardFragment();
            id="Leaderboard";
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
            String url;
            if (privacyPolicy!=null)
                url=privacyPolicy;
            else
                url = getResources().getString(R.string.privacy_policy_link);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (itemId == R.id.nav_logout) {
            createDialog();
        }
        if (frag != null && id!=null) {
            changeFragment(frag,id);
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

    private void changeFragment(Fragment frag,String id){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, frag,id); // replace a Fragment with Frame Layout
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
        SharedPreferences sharedPreferences = getSharedPreferences("MyPref",MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public FirebaseAuth getFirebaseAuth(){
        return firebaseAuth;
    }
}
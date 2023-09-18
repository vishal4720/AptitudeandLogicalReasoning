package com.developingmind.aptitudeandlogicalreasoning;

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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.developingmind.aptitudeandlogicalreasoning.home.AptitudeFragment;
import com.developingmind.aptitudeandlogicalreasoning.leaderboard.LeaderboardFragment;
import com.developingmind.aptitudeandlogicalreasoning.login.LoginActivity;
import com.developingmind.aptitudeandlogicalreasoning.profile.ProfileEnum;
import com.developingmind.aptitudeandlogicalreasoning.profile.ProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    TextView headerTitle;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        int permissionState = ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS);
        // If the permission is not granted, request it.
        if (permissionState == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View headerView = navigationView.getHeaderView(0);
        headerTitle = headerView.findViewById(R.id.header_title);

        navigationView.bringToFront();
        navigationView.setItemIconTintList(null);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        setHeaderTitle();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, new AptitudeFragment()).commit();
        }

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
                        Map<String,Object> map = documentSnapshot.getData();
                        String title = map.get(ProfileEnum.fname.toString()).toString() + " " + map.get(ProfileEnum.lname.toString()).toString()
                                + "\n" + firebaseUser.getEmail().toString();
                        headerTitle.setText(title);
                        Log.d("Email",title);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        headerTitle.setText("Name\nEmail");
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
            super.onBackPressed();
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
        } else if (itemId == R.id.nav_profile) {
            setTitle("Profile");
            frag = new ProfileFragment();
        } else if (itemId == R.id.nav_leaderboard) {
            setTitle("Leaderboard");
            frag = new LeaderboardFragment();
        } else if (itemId == R.id.nav_logout) {
            logOut();
        }
        if (frag != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame, frag); // replace a Fragment with Frame Layout
            transaction.commit(); // commit the changes
            drawerLayout.closeDrawers(); // close the all open Drawer Views
            return true;
        }
        return false;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    private void logOut(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
package com.geek.anomeet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.geek.anomeet.R;
import com.geek.anomeet.databinding.ActivityMainBinding;
import com.geek.anomeet.models.User;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

public class MainActivity extends AppCompatActivity {

   ActivityMainBinding binding;
   FirebaseAuth auth;
   FirebaseDatabase database;
   long coins = 0;
   String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
   private  int requestCode = 1;
    User user;
    KProgressHUD progress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });






        progress = KProgressHUD.create(this);
        progress.setDimAmount(0.5f);
        progress.show();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentuser = auth.getCurrentUser();


        database.getReference().child("profiles")
                .child(currentuser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        progress.dismiss();

                        user = snapshot.getValue(User.class);

                        coins = user.getCoins();

                        binding.coins.setText("You have: "  + coins);

                        Glide.with(MainActivity.this)
                                .load(user.getProfile())
                                .into(binding.profile);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.findBtutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPermissionsGranted()) {
                    if (coins > 5) {

                        coins = coins - 5;
                        database.getReference().child("profiles")
                                .child(currentuser.getUid())
                                .child("coins")
                                .setValue(coins);

                        Intent intent = new Intent(MainActivity.this, ConnectingActivity.class);
                        intent.putExtra("profile",user.getProfile());
                        startActivity(intent);

                        //startActivity(new Intent(MainActivity.this, ConnectingActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Insufficient Cooins", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    askPermissions();
                }
            }
        });

        binding.rewardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RewardActivity.class));
            }
        });

    }


    void askPermissions(){
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }


    private boolean isPermissionsGranted(){

         for ( String permissions : permissions){
             if(ActivityCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED)
                 return false;
         }

         return true;
    }

}
package com.example.fourxmessenger;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class splash extends AppCompatActivity {
    ImageView logo;
    TextView name,own1,own2;
    Animation topAnime,bottomAnime;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        logo=findViewById(R.id.logoimg);
        name=findViewById(R.id.logonameimg);
        own1=findViewById(R.id.ownone);
        own2=findViewById(R.id.owntwo);

        topAnime= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnime=AnimationUtils.loadAnimation(this,R.anim.bottom_animation);


        name.setAnimation(bottomAnime);
        own1.setAnimation(bottomAnime);
        own2.setAnimation(bottomAnime);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(splash.this,login.class);
                startActivity(intent);
                finish();
            }
        },4000);
    }
}
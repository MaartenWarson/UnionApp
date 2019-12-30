package be.pxl.unionapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import be.pxl.unionapp.R;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intentToLoginActivity = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intentToLoginActivity);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}

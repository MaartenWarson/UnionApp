package be.pxl.unionapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import be.pxl.unionapp.R;

// Dit is het welkomsscherm van de applicatie
public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Het creëeren en gebruiken van de intent wordt uitgesteld met 2000 milliseconden, dit is de tijd dat dit splash screen getoond wordt
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

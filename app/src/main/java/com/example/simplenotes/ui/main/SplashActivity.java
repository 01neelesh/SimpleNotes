package com.example.simplenotes.ui.main;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.simplenotes.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // 2 seconds
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Preload resources to avoid jank
        getResources().getDrawable(R.drawable.splash_gradient_bg, null);
        getResources().getDrawable(R.drawable.logo_glow_bg, null);
        getResources().getDrawable(R.drawable.text_gradient, null);
        getResources().getDrawable(R.drawable.heart_icon, null);

        // Start animations on a separate thread to avoid main thread overload
        new Thread(this::setupAnimations).start();

        // Navigate after delay without fade-out
        handler.postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            // Use a simple transition without fade
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }, SPLASH_DELAY);
    }

    private void setupAnimations() {
        runOnUiThread(() -> {
            ImageView heartIcon = findViewById(R.id.heart_icon);
            if (heartIcon != null) {
                ObjectAnimator pulseAnimation = ObjectAnimator.ofPropertyValuesHolder(
                        heartIcon,
                        PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.2f, 1.0f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.2f, 1.0f)
                );
                pulseAnimation.setDuration(800);
                pulseAnimation.setRepeatCount(ObjectAnimator.INFINITE);
                pulseAnimation.start();
            }

            TextView developerName = findViewById(R.id.developer_name);
            if (developerName != null) {
                developerName.setAlpha(0f);
                developerName.animate()
                        .alpha(1f)
                        .setDuration(1000)
                        .setStartDelay(300)
                        .start();
            }

            ImageView splashLogo = findViewById(R.id.splash_logo);
            if (splashLogo != null) {
                ObjectAnimator rotationAnimation = ObjectAnimator.ofFloat(splashLogo, "rotation", 0f, 360f);
                rotationAnimation.setDuration(2000);
                rotationAnimation.setRepeatCount(ObjectAnimator.INFINITE);
                rotationAnimation.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
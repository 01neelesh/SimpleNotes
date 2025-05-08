package com.example.simplenotes.ui.main;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.simplenotes.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preloadResources();
        setupAnimations();

        handler.postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DELAY);
    }

    private void preloadResources() {
        getResources().getDrawable(R.drawable.splash_gradient_bg, null);
        getResources().getDrawable(R.drawable.logo_glow_bg, null);
        getResources().getDrawable(R.drawable.text_gradient, null);
        getResources().getDrawable(R.drawable.heart_icon, null);
    }

    private void setupAnimations() {
        runOnUiThread(() -> {
            ImageView heartIcon = findViewById(R.id.heart_icon);
            if (heartIcon != null) {
                ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1f, 1.2f);
                scaleAnimator.setDuration(800);
                scaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
                scaleAnimator.setRepeatMode(ValueAnimator.REVERSE);
                scaleAnimator.addUpdateListener(animation -> {
                    float scale = (float) animation.getAnimatedValue();
                    heartIcon.setScaleX(scale);
                    heartIcon.setScaleY(scale);
                });
                scaleAnimator.start();
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
                ObjectAnimator rotate = ObjectAnimator.ofFloat(splashLogo, "rotation", 0f, 360f);
                rotate.setDuration(2000);
                rotate.setRepeatCount(ValueAnimator.INFINITE);
                rotate.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}

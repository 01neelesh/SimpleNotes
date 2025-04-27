package com.example.simplenotes.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.simplenotes.R;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;
    private boolean loadingComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("SimpleNotesPrefs", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);

        // Set splash screen condition
        if (isFirstLaunch) {
            // First launch: Skip splash delay, go straight to onboarding
            loadingComplete = true;
        } else {
            // Subsequent launches: Show splash screen for 2 seconds
            splashScreen.setKeepOnScreenCondition(() -> !loadingComplete);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                loadingComplete = true;
                navigateToNotesIfNotFirstLaunch();
            }, 2000); // 2-second delay
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Delay NavController setup until view is ready
        final ViewTreeObserver viewTreeObserver = findViewById(R.id.nav_host_fragment).getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment);
                appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
                NavigationUI.setupActionBarWithNavController(MainActivity.this, navController, appBarConfiguration);

                // Remove the listener using a fresh ViewTreeObserver
                findViewById(R.id.nav_host_fragment).getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Navigate if not first launch, now that NavController is ready
                if (!prefs.getBoolean("isFirstLaunch", true)) {
                    navigateToNotesIfNotFirstLaunch();
                }
            }
        });
    }

    private void navigateToNotesIfNotFirstLaunch() {
        SharedPreferences prefs = getSharedPreferences("SimpleNotesPrefs", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);
        if (!isFirstLaunch && navController != null) {
            // Only navigate if we're not already on notesFragment
            if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.notesFragment) {
                navController.navigate(R.id.action_onboardingFragment_to_notesFragment);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
package com.example.simplenotes.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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


        splashScreen.setKeepOnScreenCondition(() -> !loadingComplete);

        setContentView(R.layout.activity_main);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Do any task here, for example:
            // network call, read database etc.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            loadingComplete = true;
        }, 0);

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            // Initialize NavController
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    navController = Navigation.findNavController(this, R.id.nav_host_fragment);
                    appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
                    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

                } catch (IllegalArgumentException e) {
                    Log.e("MainActivity", "NavController not found", e);
                }
            });

        }


        @Override
        public boolean onSupportNavigateUp () {
            // Handle navigate up with NavController
            return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
        }


    }

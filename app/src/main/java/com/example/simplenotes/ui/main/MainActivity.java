package com.example.simplenotes.ui.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("SimpleNotesPrefs", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);

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

                findViewById(R.id.nav_host_fragment).getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Navigate based on first launch
                if (isFirstLaunch && navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() != R.id.onboardingFragment) {
                    navController.navigate(R.id.onboardingFragment);
                } else if (!isFirstLaunch) {
                    navigateToNotesIfNotFirstLaunch();
                }
            }
        });
    }

    private void navigateToNotesIfNotFirstLaunch() {
        if (navController != null && navController.getCurrentDestination() != null) {
            if (navController.getCurrentDestination().getId() != R.id.notesFragment) {
                navController.navigate(R.id.notesFragment);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
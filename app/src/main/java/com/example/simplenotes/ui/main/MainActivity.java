package com.example.simplenotes.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        // Check if user is signed in
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account == null) {
//            startSignInActivity();
//            return;
//        }

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
    public boolean onSupportNavigateUp() {
        // Handle navigate up with NavController
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

//    private void startSignInActivity() {
//        Intent intent = new Intent(this, SignInActivity.class);
//        startActivity(intent);
//        finish();
//    }
}

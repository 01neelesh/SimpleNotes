//package com.example.simplenotes.firebasesetup;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.simplenotes.ui.main.MainActivity;
//import com.example.simplenotes.R;
//import com.google.android.gms.common.SignInButton;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.BuildConfig;
//
//public class SignInActivity extends AppCompatActivity {
//
//    GoogleSignInOptions gso;
//    GoogleSignInClient gsc;
//    SignInButton signInButton;
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_in);
//
//        boolean bypassLogin = BuildConfig.DEBUG;
//        bypassLogin = true;
//
//        if (bypassLogin) {
//            Log.d("SignInActivity", "Skipping login process");
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }
//
//
//
//        // Initialize Google Sign-In options
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        gsc = GoogleSignIn.getClient(this, gso);
//
//        // Check if the user is already signed in
//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//        if (acct != null) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
//        // Initialize the sign-in button and set the click listener
//        signInButton = findViewById(R.id.sign_in_button);
//        if (signInButton != null) {
//            signInButton.setOnClickListener(v -> signIn());
//        } else {
//            Log.e("SignInActivity", "signInButton is null");
//        }
//    }
//
//    private void signIn() {
//        Intent signInIntent = gsc.getSignInIntent();
//        startActivityForResult(signInIntent, 1000);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1000) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }
//
//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            if (account != null) {
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        } catch (ApiException e) {
//            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
//        }
//    }
//}

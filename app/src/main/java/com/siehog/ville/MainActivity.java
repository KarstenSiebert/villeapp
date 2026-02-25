package com.siehog.ville;

import static com.siehog.ville.httpclient.KeyHelper.ensureKeyPairExists;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import com.siehog.ville.databinding.ActivityMainBinding;
import java.util.UUID;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            ensureKeyPairExists();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("market_id");
        editor.apply();

        String id = prefs.getString("device_id", null);

        if (id == null) {
            id = UUID.randomUUID().toString();
            prefs.edit().putString("device_id", id).apply();
        }

        // authenticateUser();

        setSupportActionBar(binding.appBarMain.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNavigationView = binding.appBarMain.contentMain.bottomNavView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_transform, R.id.nav_scanner)
                    .build();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void authenticateUser() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(
                this,
                executor,
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                   }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        finish();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        finish();
                    }
                }
        );

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock App")
                .setSubtitle("Authenticate with your device PIN, pattern, or password")
                .setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

}
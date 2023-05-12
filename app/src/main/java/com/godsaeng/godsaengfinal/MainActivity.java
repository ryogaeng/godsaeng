package com.godsaeng.godsaengfinal;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import com.godsaeng.godsaengfinal.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    //360X781
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View decorView = getWindow().getDecorView();
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), decorView);
        controller.setAppearanceLightStatusBars(true);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
       /* BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);*/
    }

    @Override
    public void onBackPressed() {
        NavDestination navDestination = navController.getCurrentDestination();
        if (navDestination != null) {
            int currentId = navDestination.getId();
            Log.d("NavDebug", "currentId=" + currentId);
            if (currentId == R.id.navigation_first || currentId == R.id.navigation_second ||
                    currentId == R.id.navigation_third || currentId == R.id.navigation_fourth ) {
                if (isBackPressedTwice()) {
                    finish();
                } else {
                    Snackbar.make(binding.getRoot(), "한번 더 누르시면 종료합니다.", Snackbar.LENGTH_LONG).show();
                }
                return;
            }
        }
        super.onBackPressed();
    }

    private long mLastClickTime = 0;

    public boolean isBackPressedTwice(){
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
            return true;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return false;
    }
}
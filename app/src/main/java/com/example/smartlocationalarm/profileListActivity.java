package com.example.smartlocationalarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.concurrent.Executor;

import hotchemi.android.rate.AppRate;

public class profileListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nv;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_list);
//        AppRate.with(this)
//                .setInstallDays(0)
//                .setLaunchTimes(10)
//                .setRemindInterval(5)
//                .setShowLaterButton(true)
//                .monitor();
//        AppRate.showRateDialogIfMeetsConditions(this);
        final Context c = this;
        final Activity a = this;
        String id = getIntent().getStringExtra("id");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        nv = findViewById(R.id.navView);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.create:
                        clickCreate();
                        return true;

                    case R.id.exist:
                        existing();
                        return true;

                    case R.id.allalarms:
                        Intent intent3 = new Intent(profileListActivity.this, profileListActivity.class);
                        startActivity(intent3);
                        return true;
                    case R.id.setting:
                        Intent intent4 = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent4);
                        return true;
//                    case R.id.rating:
//                        AppRate.with(c).showRateDialog(a);
//                        return true;
//                    case R.id.share:
//                        Intent i = new Intent(Intent.ACTION_SEND);
//                        i.setType("text/plain");
//                        i.putExtra(Intent.EXTRA_SUBJECT, "Smart Location Alarm");
//                        String message = "\nJe voudrais vous recommander cette application Smart Location Alarm, prochainement nous allons la mettre dans le play store \n\n";
//
//                        i.putExtra(Intent.EXTRA_TEXT, message);
//                        startActivity(Intent.createChooser(i, "Share with others"));
//                        return true;
                    case R.id.about:
                        Intent intent5 = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(intent5);
                        return true;
                    default:
                        return false;
                }
            }
        });
        showAlarms();

        // treat id heeere
        String s = ((MyApplication) this.getApplication()).getId();
        Toast.makeText(getApplicationContext(),
                "id: " + s, Toast.LENGTH_SHORT)
                .show();
        // After removing the alarm set




        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(profileListActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Error while Authenticating" + errString, Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(profileListActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
               Toast.makeText(getApplicationContext(),
                        "Authentication Successful !", Toast.LENGTH_SHORT).show();
                showAlarms();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication Failed !",
                        Toast.LENGTH_SHORT)
                        .show();
                Intent intent = new Intent(profileListActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        rePromptInfo();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        int id;

        if (intent != null) {
            id = intent.getExtras().getInt("id");
            Toast.makeText(getApplicationContext(),
                    "id: " + id, Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(getApplicationContext(),
                    "intent is null  ", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void existing() {
        Intent intent = new Intent(this, MapsActivity.class);
        this.startActivity(intent);
    }

    private void clickCreate() {
        Intent intent = new Intent(this, PermissionActivity.class);
        this.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void rePromptInfo() {
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Only you can see your alarms !")
                .setSubtitle("Log in using your fingerprint")
                .setDeviceCredentialAllowed(true)
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    public void showAlarms() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_alarms);
        try {
            recyclerView.addItemDecoration(new myDeviderItemDecoration(LinearLayoutManager.VERTICAL, this, 30));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String uniqueId = prefs.getString("UUID", "alarm");
        new firebaseDatabaseHelper(profileListActivity.this).readAlarms(new firebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<alarm> alarms, List<String> keys) {
                new RecyclerView_Config().setConfig(recyclerView, profileListActivity.this, alarms, keys);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }

}

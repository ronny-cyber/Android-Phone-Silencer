package com.example.smartlocationalarm;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class Welcome_Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nv;
    // Test Are

    // End Test Area
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_);
        // Test Area
        Intent intent = new Intent(Welcome_Activity.this, BackgroundLocationUpdateService.class);

        NotificationManager nm= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (!nm.isNotificationPolicyAccessGranted()){
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
        }

        final Context c = this;
        final Activity a = this;

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
                        Intent intent3 = new Intent(Welcome_Activity.this, profileListActivity.class);
                        startActivity(intent3);
                        return true;
                    case R.id.setting:
                        Intent intent4 = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent4);
                        return true;
//
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
        startService(intent);
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
        new firebaseDatabaseHelper(Welcome_Activity.this).readAlarms(new firebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<alarm> alarms, List<String> keys) {
                Log.d("alarmdata", "DataIsLoaded: "+alarms.toString());
                    new RecyclerView_configMain().setConfig(recyclerView, Welcome_Activity.this, alarms, keys);
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

    private void existing() {
        Intent intent = new Intent(this, MapsActivity.class);
        this.startActivity(intent);
    }

    private void clickCreate(){
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
}

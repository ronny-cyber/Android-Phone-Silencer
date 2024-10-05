package com.example.smartlocationalarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import hotchemi.android.rate.AppRate;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        AppRate.with(this)
                .setInstallDays(0)
                .setLaunchTimes(10)
                .setRemindInterval(5)
                .setShowLaterButton(true)
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);
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
                        Intent intent3 = new Intent(AboutActivity.this, profileListActivity.class);
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

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.logo)
                .setDescription(getString(R.string.desc))
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Contact")
                .addEmail("ayushbhavsar299@gmail.com")
               .addWebsite("https://github.com/AryanP45/Android-phone-silencer")
                .create();

        ViewGroup aboutView = findViewById(R.id.aboutView);
        aboutView.addView(aboutPage);

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

package com.example.smartlocationalarm;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import hotchemi.android.rate.AppRate;

public class profileDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    private EditText editName, editNotes, editRadius;
    private ImageButton btn_edit, btn_delete;
    private String name, note, radius, key, longitude, latitude;
    private boolean status;
    private Switch _switch;
    private Boolean _Statut;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
//        AppRate.with(this)
//                .setInstallDays(0)
//                .setLaunchTimes(10)
//                .setRemindInterval(5)
//                .setShowLaterButton(true)
//                .monitor();
//        AppRate.showRateDialogIfMeetsConditions(this);
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
                        Intent intent3 = new Intent(profileDetailsActivity.this, profileListActivity.class);
                        startActivity(intent3);
                        return true;
                    case R.id.setting:
                        Intent intent4 = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent4);
                        return true;

                    case R.id.about:
                        Intent intent5 = new Intent(getApplicationContext(), AboutActivity.class);

                        startActivity(intent5);
                        return true;
                    default:
                        return false;
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        key = getIntent().getStringExtra("key");
        name = getIntent().getStringExtra("name");
        note = getIntent().getStringExtra("notes");
        radius = getIntent().getStringExtra("radius");
        longitude = getIntent().getStringExtra("longitude");
        latitude = getIntent().getStringExtra("latitude");
        status = getIntent().getExtras().getBoolean("status");


        editName = findViewById(R.id.editName);
        editNotes = findViewById(R.id.editNotes);
        editRadius = findViewById(R.id.editradius);
        _switch = findViewById(R.id.switch3);

        _switch.setChecked(status);
        editName.setText(name);
        editNotes.setText(note);
        editRadius.setText(radius);

        btn_delete = findViewById(R.id.deletebtn);
        btn_edit = findViewById(R.id.editBtn);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        final String uniqueId = prefs.getString("UUID", "alarm");

        _switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    status = true;

                } else {
                    status = false;
                }
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(profileDetailsActivity.this);
                builder.setMessage("are you sure you want to edit this alarm ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alarm alarm = new alarm();
                        alarm.setName(editName.getText().toString());
                        alarm.setNotes(editNotes.getText().toString());
                        alarm.setRadius(editRadius.getText().toString());
                        alarm.setLongitude(longitude);
                        alarm.setLatitude(latitude);
                        alarm.setStatus(status);

                        new firebaseDatabaseHelper(profileDetailsActivity.this).updateAlarm(key, alarm, new firebaseDatabaseHelper.DataStatus() {
                            @Override
                            public void DataIsLoaded(List<com.example.smartlocationalarm.alarm> alarms, List<String> keys) {

                            }

                            @Override
                            public void DataIsInserted() {

                            }

                            @Override
                            public void DataIsUpdated() {
                                Toast.makeText(profileDetailsActivity.this, "alarm has been updated successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(profileDetailsActivity.this, profileListActivity.class);
                                startActivity(intent);

                            }

                            @Override
                            public void DataIsDeleted() {

                            }
                        });

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(profileDetailsActivity.this);
                builder.setMessage("are you sure you want to delete this alarm ?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new firebaseDatabaseHelper(profileDetailsActivity.this).deleteAlarm(key, new firebaseDatabaseHelper.DataStatus() {
                            @Override
                            public void DataIsLoaded(List<alarm> alarms, List<String> keys) {

                            }

                            @Override
                            public void DataIsInserted() {

                            }

                            @Override
                            public void DataIsUpdated() {

                            }

                            @Override
                            public void DataIsDeleted() {
                                Toast.makeText(profileDetailsActivity.this, "alarm has been deleted successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(profileDetailsActivity.this, profileListActivity.class);
                                startActivity(intent);

                            }
                        });

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
        
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, profileListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //add marker in alarm position
        LatLng alarmPosition = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        mMap.addMarker(new MarkerOptions().position(alarmPosition).title(name).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_marker))).showInfoWindow();
        mMap.addCircle(new CircleOptions().center(alarmPosition).radius(Double.parseDouble(radius)).strokeWidth(2.0f).strokeColor(getResources().getColor(R.color.outline)).fillColor(getResources().getColor(R.color.radius)));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(alarmPosition));
        moveCamera(alarmPosition, 15f);
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d("Maps activity", "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    public BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDarawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDarawable.setBounds(0, 0, vectorDarawable.getIntrinsicWidth(), vectorDarawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDarawable.getIntrinsicWidth(), vectorDarawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDarawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    private void existing() {
        Intent intent = new Intent(this, MapsActivity.class);
        this.startActivity(intent);
    }

    private void clickCreate() {
        Intent intent = new Intent(this, profileListActivity.class);
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

package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class NavigationForAllActivities {
    public static void SetNavigationForActivities(DrawerLayout drawerLayout,
                                                  NavigationView navigationView,
                                                  Activity activity, Customer currentCustomer) {


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Intent intent;
                if (itemId == R.id.home_page) {
                    intent = new Intent(activity, MainActivity.class);
                    intent.putExtra("customer",currentCustomer);
                    activity.startActivity(intent);
                } else if (itemId == R.id.create_sport_activity) {
                    intent = new Intent(activity, CreateSportActivity.class);
                    intent.putExtra("customer",currentCustomer);
                    activity.startActivity(intent);
                } else if (itemId == R.id.view_sport_activity) {
                    intent = new Intent(activity, MySportingActivitiesActivity.class);
                    intent.putExtra("customer",currentCustomer);
                    activity.startActivity(intent);
                } else if (itemId == R.id.exit_app) {
                    activity.finish();
                } else if (itemId == R.id.logout) {
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }}
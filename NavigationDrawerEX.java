package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class NavigationDrawerEX extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Context context;
    private Customer currentCustomer;
    private MaterialToolbar materialToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer_ex);
        drawerLayout = findViewById(R.id.DrawerLayout);
        navigationView = findViewById(R.id.NavigationView);
        context = NavigationDrawerEX.this;
        Intent intent = getIntent();
        currentCustomer = (Customer) intent.getSerializableExtra("customer");
        materialToolbar = findViewById(R.id.materialToolbar);
        NavigationForAllActivities.SetNavigationForActivities(drawerLayout,
                navigationView, this, currentCustomer);

    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(navigationView)){
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }

}

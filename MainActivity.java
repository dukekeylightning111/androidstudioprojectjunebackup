package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.maps.MapsInitializer;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity  {
    private TextView sign_in_or_greet;
    private Button aboutButton;
    private Customer currentCustomer;
    private Context context;
    private DrawerLayout drawerLayout;
    private Button sign_or_log_btn;
    private Button view_sport_activities;
    private AppDatabase app_db;
    private Button createSportActivity;
    private CustomerDao customerDao;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        init();
        func_handle_sign_or_log_in();
        func_sign_in_or_greet();
        func_create_sport_activity();
        func_view_sport_activities();
        func_view_about();
    }

    public void func_view_about() {
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ProjectInfoActivity = new Intent(context, ProjectInfoActivity.class);
                startActivity(ProjectInfoActivity);
            }
        });
    }

    public void func_view_sport_activities() {
        view_sport_activities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentCustomer == null) {
                    Toast.makeText(context, "אנא הרשמו קודם (אין לכם משתמש, אין לכם פעולות)", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (currentCustomer == null) {
                                        Toast.makeText(context, "הלקוח לא נמצא ", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Intent view_sport_activities = new Intent(context, MySportingActivitiesActivity.class);
                                    view_sport_activities.putExtra("customer", currentCustomer);
                                    startActivity(view_sport_activities);
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    public void func_handle_sign_or_log_in() {
        sign_or_log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WelcomeUserActivity.class);
                if (currentCustomer != null) {
                    intent.putExtra("customer", currentCustomer);
                    Toast.makeText(context, currentCustomer.getName() + "", Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                }, 10);
            }
        });
    }

    public void func_sign_in_or_greet() {
        if (currentCustomer != null) {
            sign_in_or_greet.setText("שלום" + " " + currentCustomer.getName() + " " + " ברוכים הבאים ");
        }
    }

    public void func_create_sport_activity() {
        createSportActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentCustomer == null) {
                    Toast.makeText(context, "אתם צריכים להירשם", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent_create_sport_activity = new Intent(context, CreateSportActivity.class);
                    intent_create_sport_activity.putExtra("customer", currentCustomer);
                    startActivity(intent_create_sport_activity);
                }
            }
        });
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void init() {
        context = getApplicationContext();
        sign_in_or_greet = findViewById(R.id.sign_in_or_greet);
        app_db = AppDatabase.getDatabase(this);
        customerDao = app_db.customerDAO();
        view_sport_activities = findViewById(R.id.view_sport_activities);
        createSportActivity = findViewById(R.id.create_sport_activity);
        Intent intent = getIntent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                currentCustomer = (Customer) intent.getSerializableExtra("customer");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentCustomer != null) {
                            sign_in_or_greet.setText("שלום " + currentCustomer.getName() + " ברוכים הבאים ");
                        }
                    }
                });
            }
        }).start();
        sign_or_log_btn = findViewById(R.id.sign_in);
        aboutButton = findViewById(R.id.aboutButton);
        drawer = findViewById(R.id.my_drawer_layout);
        navigationView = findViewById(R.id.nav_menu);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.nav_open, R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        context = getApplicationContext();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationForAllActivities.SetNavigationForActivities(drawerLayout,
                navigationView, this, currentCustomer);

    }

    public void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("יציאה");
        builder.setMessage("האם אתם בטוחים לגבי לצאת מהאפליקציה?");
        builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("התנתקות");
        builder.setMessage("האם אתם בטוחים לגבי ההתנקות");
        builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentCustomer = null;
                Toast.makeText(context, "התנתקתם מהאפליקציה", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

/* private Button createSportActivity;
     private TextView greet_user;
     private Customer currentCustomer;
     private CustomerDao customerDao;
     private AppDatabase appDatabase;
     private Boolean is_customer_signedIn;
     private LiveData<Customer> customerLiveData;*/
 /*  public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }
      /*drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        if (id == R.id.home_page) {
         Intent homePageIntent = new Intent(this, MainActivity.class);
            homePageIntent.putExtra("customer", currentCustomer);
            startActivity(homePageIntent);
            return true;
        } else if (id == R.id.logout) {
             showLogoutConfirmationDialog();
            return true;
        } else if (id == R.id.exit_app) {
            showExitConfirmationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }*/
  /*  public void createSportActivityClicked(){
        createSportActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private void handle_customer_sign_in() {
        customerLiveData = customerDao.getAllCustomers();
        customerLiveData.observe(this, new Observer<Customer>() {
            @Override
            public void onChanged(Customer customer) {
                if (customer != null) {
                    currentCustomer = customer;
                    greet_user.setText("Welcome, " + customer.getName() + "!");
                    is_customer_signedIn = true;
                } else {
                    greet_user.setText("please sign in");
                    is_customer_signedIn = false;
                }
                greet_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent_sign_in = new Intent(MainActivity.this, Sign_In.class);
                        startActivity(intent_sign_in);
                    }
                });
            }

        });

        createSportActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_customer_signedIn) {
                    // Launch CreateSportActivity
                    //Intent intent = new Intent(MainActivity.this, CreateSportActivity.class);
                    //startActivity(intent);
                    Toast.makeText(MainActivity.this, "wiat one sec before that pls", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MainActivity.this, "Please sign in first", Toast.LENGTH_SHORT).show();
                    handle_customer_sign_in();
                }
            }
        });
    }
    public void init(){
        createSportActivity = findViewById(R.id.create_sport_activity);
        greet_user = findViewById(R.id.sign_in_or_greet);
        appDatabase = AppDatabase.getDatabase(this);
        is_customer_signedIn = false;
        customerDao = appDatabase.customerDAO();
        customerDao.getAllCustomers().observe(MainActivity.this, customers -> {
            if (customers != null) {
                customerLiveData = customerDao.getLatestCustomer();
                if (customerLiveData != null) {
                    currentCustomer = customerLiveData.getValue();
                    if (currentCustomer != null) {
                        greet_user.setText(currentCustomer.getName() + " welcome!");
                    } else {
                        //Intent intent_sign_in = new Intent(MainActivity.this, Sign_In.class);
                        //startActivity(intent_sign_in);

                    }
                }
            } else {
                is_customer_signedIn = false;
            }

            if(is_customer_signedIn == false){
                greet_user.setText("please sign in");
            }
        });
    }
}*/
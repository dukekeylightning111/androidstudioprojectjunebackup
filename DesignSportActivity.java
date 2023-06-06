package com.example.myapplication;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DesignSportActivity extends AppCompatActivity implements LocationListener, Serializable {
    private String category;
    private TextView info_sport_category;
    private String selectedLocation;
    private FragmentResultListener fragmentResultListener;
    private Button time_button;
    private String formattedDate;
    private Button create_sport_activity;
    private String time;
    private String date;
    private String temp;
    private String message;
    private String title;
    private int PERMISSION_REQUEST_CODE = 1;
    private EditText editText_title;
    private Calendar calendar;
    private Button getDate;
    private String description;
    private EditText editText_sport_act_desc;
    private SportingActivityClass sportingActivityClass;
    private CustomerDao customerDao;
    private AppDatabase app_db;
    private Button btn_to_see_sport_activities;
    private int hour;
    private int minute;
    private Button get_location;
    private ActionBarDrawerToggle toggle;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private SportingActivityClassDao sportingActivityClassDao;
    private Customer currentCustomer;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_sport);
        Intent intent = getIntent();
        init();
        getDate();
        if (checkPermissions()) {
             func_get_location();
        } else {
            requestPermissions();
        }
        func_createSportActivity();
        func_expand_cv();
        func_view_sport_activities();
        getSupportFragmentManager().setFragmentResultListener("map_result", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if (requestKey.equals("map_result")) {
                    String selectedAddress = result.getString("selected_address");

                    // Update the address TextView with the selected address
                    get_location.setText( "מיקום:"+ selectedAddress);
                    selectedLocation = selectedAddress;
                }
            }
        });
    }

    public void setFragmentResultListener(String resultKey, FragmentResultListener fragmentResultListener) {
        getSupportFragmentManager().setFragmentResultListener(resultKey, this, fragmentResultListener);
    }

    public void showTimePickerDialog(View v) {
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, timeSetListener, currentHour, currentMinute, DateFormat.is24HourFormat(this));
        dialog.show();
    }

    public TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedTime.set(Calendar.MINUTE, minute);

            Calendar currentTime = Calendar.getInstance();

            if (selectedTime.before(currentTime)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DesignSportActivity.this);
                builder.setTitle("זמן פעולה אינו תקין");
                builder.setMessage("בחרתם זמן שכבר עבר.");
                builder.setPositiveButton("אישור", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                time_button.setText("זמן פעולה: " + time + " לחצו עבור לשנות");
            }
        }
    };


    private boolean checkPermissions() {
        int coarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int internetPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        int networkStatePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE);

        return coarseLocationPermission == PackageManager.PERMISSION_GRANTED &&
                fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                internetPermission == PackageManager.PERMISSION_GRANTED &&
                networkStatePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
        }, PERMISSION_REQUEST_CODE);
    }

    public void func_expand_cv() {
        getDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderCustomised = new AlertDialog.Builder(DesignSportActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                Calendar currentDate = Calendar.getInstance();
                int year = currentDate.get(Calendar.YEAR);
                int month = currentDate.get(Calendar.MONTH);
                int day = currentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DesignSportActivity.this, new
                        DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, selectedYear);
                                calendar.set(Calendar.MONTH, selectedMonth);
                                calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                                if (calendar.before(currentDate)) {
                                    AlertDialog alertDialog = builderCustomised.setTitle("תאריך אינו תקין")
                                            .setMessage("אנא בחרו תאריך תקין (תאריך היום או אחרי)")
                                            .setPositiveButton("אישור", null)
                                            .create();
                                    alertDialog.show();
                                    return;
                                }

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String format_date = dateFormat.format(calendar.getTime());
                                date = format_date;
                                getDate.setText("תאריך פעולה:" + date + " לחצו עבור לשנות");
                                formattedDate = date;
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    public void func_createSportActivity() {
        create_sport_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp = "";
                if (check_variables() == false) {
                    if (title.isEmpty()) {
                        message = "כותרת חסרה, צריך למלא חלק זה";
                        temp += message + "\n";
                    }
                    if (category.isEmpty()) {
                        message = "קטגוריה חסרה, צריך למלא חלק זה";
                        temp += message + "\n";
                    }
                    if (time.isEmpty()) {
                        message = "זמן פעולה חסר , צריך למלא חלק זה";
                        temp += message + "\n";
                    }
                    if (description.isEmpty()) {
                        message = "תיאור פעולה חסר, ץריך למלא חלק זה";
                        temp += message + "\n";
                    }
                    if (formattedDate.isEmpty()) {
                        message = "תאריך פעולה חסר, צריך למלא חלק זה ";
                        temp += message + "\n";
                    } if (selectedLocation == null) {
                        message = "מיקום פעולה חסר, צריך למלא חלק זה ";
                        temp += message + "\n";
                    }
                    new AlertDialog.Builder(DesignSportActivity.this)
                            .setTitle("פרטים חסרים")
                            .setMessage(temp)
                            .setPositiveButton("אישור", null)
                            .show();
                } else {



                    new AlertDialog.Builder(DesignSportActivity.this)
                            .setTitle("אישור יצירת פעילות ספורט")
                            .setMessage("האם אתה בטוח שברצונך ליצור פעילות ספורט?\n\nפרטי הפעילות שהוזנו:\nכותרת: " + title + "\nקטגוריה: " + category + "\nתיאור: " + description + "\nזמן: " + time + "\nתאריך: " + formattedDate + "\n מיקום" + selectedLocation)
                            .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    sportingActivityClass = new SportingActivityClass(title, category, currentCustomer, time, description, selectedLocation, formattedDate);
                                    currentCustomer.addSportingActivity(sportingActivityClass);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            currentCustomer.addSportingActivity(sportingActivityClass);
                                            sportingActivityClassDao.insert(sportingActivityClass);
                                        }
                                    }).start();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new AlertDialog.Builder(DesignSportActivity.this)
                                                    .setMessage("פעולה נוצרה בהצלחה")
                                                    .setPositiveButton("אישור", null)
                                                    .show();
                                            editText_title.setText("");
                                            get_location.setText("קבעו מיקום");
                                            editText_sport_act_desc.setText("");
                                            time_button.setText("קבעו זמן לפעולה");
                                            getDate.setText("קבעו תאריך לפעולה");
                                        }
                                    });
                                }

                            })
                            .setNegativeButton("לא", null)
                            .show();
                }
            }
        });
    }

    public boolean check_variables() {
        if (date == null || time.isEmpty() || editText_sport_act_desc.getText().toString().isEmpty() || editText_title.getText().toString().isEmpty()) {
            return false;
        } else {
            description = editText_sport_act_desc.getText().toString();
            title = editText_title.getText().toString();
            return true;
        }
    }

    public void getDate() {
        time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (date != null) {
                    showTimePickerDialog(view);
            }
            }
        });
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                time_button.setText("זמן פעולה: " + time + " לחצו עבור לשנות");
                String selectedDateTime = "תאריך הפעולה: " + formattedDate + " " + time;
            }
        };
    }

    public void func_view_sport_activities() {
        btn_to_see_sport_activities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(DesignSportActivity.this, MySportingActivitiesActivity.class);
                I.putExtra("customer", currentCustomer);
                startActivity(I);
            }
        });
    }


    public void init() {
        drawerLayout = findViewById(R.id.my_drawer_layoutDesign);
        getDate = findViewById(R.id.getDate);
        create_sport_activity = findViewById(R.id.button_add_sport);
        category = getIntent().getStringExtra("sport");
        setTitle(category);
        editText_title = findViewById(R.id.sport_act_title);
        editText_sport_act_desc = findViewById(R.id.EditTxt_act_desc);
        app_db = AppDatabase.getDatabase(this);
        customerDao = app_db.customerDAO();
        sportingActivityClassDao = app_db.SportingActivityClassDao();
        btn_to_see_sport_activities = findViewById(R.id.btn_to_see_sport_activities);
        Intent intent = getIntent();
        currentCustomer = (Customer) intent.getSerializableExtra("customer");
        info_sport_category = findViewById(R.id.txtView_sport_act_info);
        info_sport_category.setText("פעולת ה" + category + " של " + currentCustomer.getName());
        get_location = findViewById(R.id.get_location);
        calendar = Calendar.getInstance();
        time_button = findViewById(R.id.button_confirm_date);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigationView);
        context = DesignSportActivity.this;
        NavigationForAllActivities.SetNavigationForActivities(drawerLayout,
                navigationView, this, currentCustomer);



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
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }

    public void func_get_location() {
        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapFragment();
                fragmentResultListener = new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        selectedLocation = result.getString("selected_address");
                        Toast.makeText(DesignSportActivity.this, "" + selectedLocation, Toast.LENGTH_SHORT).show();
                        get_location.setText( selectedLocation+ "זה המיקום שבחרתם:");
                    }
                };
            }
        });
    }



    public void openMapFragment() {
        MapFragment mapFragment = new MapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mapFragment, "map_fragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



       /* locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locman.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locman.requestLocationUpdates(provider, 2000, 1, this);
        my_location = "" + local.getLatitude() + " " + local.getLongitude();
*/



    @Override
    public void onLocationChanged(@NonNull Location location) {
/*
        my_location =  + location.getLatitude() + location.getLongitude();
*/

    }
    @Override
    public void onStatusChanged(String s, int i ,Bundle bundle){

    }
    @Override
    public void onProviderEnabled(String s){

    }
    @Override
    public void onProviderDisabled(String s){

    }

   /* private void loadCustomerFromDatabase(String customerName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Customer customer = customerDao.getCustomersByNameNoLD(customerName);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onCustomerLoaded(customer);
                    }
                });
            }
        }).start();
    }

    private void onCustomerLoaded(Customer customer) {
        selected_customer = customer;
        info_sport_category.setText("פעולת ה"  + category + "של"+selected_customer.getName() );
        Toast.makeText(DesignSportActivity.this, "" + customer.getName() + " ", Toast.LENGTH_SHORT).show();
    }*/



}

  /* public void func_expand_calendar(){
        calendarView.setOnClickListener(new View.OnClickListener() {
            boolean isExpanded = false;
            @Override
            public void onClick(View v) {
                if (!isExpanded) {
                    ViewGroup.LayoutParams expandedLayoutParams = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT // set to MATCH_PARENT
                    );
                    calendarView.setLayoutParams(expandedLayoutParams);
                } else {
                    calendarView.setLayoutParams(originalLayoutParams);
                }
                isExpanded = !isExpanded;
            }
        });
    }*/

/*
 */
/*public void func_get_location(){
        get_location = findViewById(R.id.get_location);
        get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DesignSportActivity.this);
                builder.setMessage("האם אתם מעוניין בלקבוע את מיקום הפעולה באמצעות כתיבת שם המקום?")
                        .setCancelable(false)
                        .setPositiveButton("כן, אכתוב את שם המקום", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AlertDialog alertDialog = (AlertDialog) dialog;
                                EditText editTextLocation = alertDialog.findViewById(R.id.editTextLocation);
                                String location = editTextLocation.getText().toString();
                            }
                        })
                        .setNegativeButton("לא, אבחר באמצעות מפה", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

         //
 */
/*func_grant_permission();
                setLocationManager();//
 */
/*

    }//


    public void func_grant_permission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    public void setLocationManager(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationEnabled();
        getLocation();
    }

    private void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(DesignSportActivity.this)
                    .setTitle("אשרו שימוש בGPS")
                    .setMessage("עבור להשתמש בחלק זה צריך להפעילה GPS.")
                    .setCancelable(false)
                    .setPositiveButton("לאשר", new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                    .setNegativeButton("לבטל", null)
                    .show();
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        my_location = location.getLatitude() + ", " + location.getLongitude();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setLocationManager();
            }
        }
    }


*/
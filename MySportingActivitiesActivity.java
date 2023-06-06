package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.NoCopySpan;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.Map;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Index;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

public class MySportingActivitiesActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ArrayList<SportingActivityClass> sportingActivityClasses;
    private Context context;
    private EditText search;
    private TextInputLayout searchLayout;
    private Customer currentCustomer;
    private AppDatabase app_db;
    private CustomerDao customerDao;
    private FloatingActionButton myFAB;
    private Customer customer;
    private SportingActivityAdapter adapter;
    private ActionBarDrawerToggle toggle;

    private ActionBarDrawerToggle actionBarDrawerToggle;

    private TextView customer_name;
    private SportingActivityClassDao sportingActivityClassDao;
    private MediatorLiveData<Map<Index.Order, SportingActivityClass>> sportingActivityLiveData
            = new MediatorLiveData<>();
    private RecyclerView recyclerView;
    public interface ItemTextWatcher extends NoCopySpan {
        public void beforeTextChanged(CharSequence s, int start,
                                      int count, int after);
        public void onTextChanged(CharSequence s, int start, int
                before, int count);
        public void afterTextChanged(Editable s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sporting_activities);
        Intent intent = getIntent();
        init();
        customer = (Customer) intent.getSerializableExtra("customer");
        customer_name.setText("הפעולות של " + customer.getName() + customer.getId());
        recyclerView = findViewById(R.id.sporting_activity_recycler_view);
        func_view_sport_activities();
        func_create_sport_activity();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int
                    after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String customerName = s.toString();
            }
        });
        sportingActivityLiveData.observe(this, new Observer<Map<Index.Order, SportingActivityClass>>() {
            @Override
            public void onChanged(Map<Index.Order, SportingActivityClass> orderSportingActivityClassMap) {
                for(Map.Entry<Index.Order, SportingActivityClass> entry : orderSportingActivityClassMap.entrySet()){
                            SportingActivityClass sportingActivityClass = new SportingActivityClass(entry.getValue().getTitle(), entry.getValue().getCategory(), entry.getValue().getCustomer(), entry.getValue().getTime(), entry.getValue().getDescription(),entry.getValue().getLocation(),entry.getValue().getDate() );
                    sportingActivityClasses.add(sportingActivityClass);
                }
            }
        });
        adapter = new SportingActivityAdapter(sportingActivityLiveData, customer, this);


    }
    public void func_create_sport_activity() {
        myFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createSportIntent = new Intent(context, CreateSportActivity.class);
                createSportIntent.putExtra("customer", customer);
                startActivity(createSportIntent);
            }
        });
    }

    public void init() {
        searchLayout = findViewById(R.id.searchitemlayout);
        search = searchLayout.getEditText();
        drawerLayout = findViewById(R.id.my_drawer_layout);
        app_db = AppDatabase.getDatabase(this);
        customerDao = app_db.customerDAO();
        customer_name = findViewById(R.id.customer_name);
        sportingActivityClassDao = app_db.SportingActivityClassDao();
        myFAB = findViewById(R.id.myFAB);
        context = getApplicationContext();
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.navigationView);
        context = MySportingActivitiesActivity.this;
        Intent intent = getIntent();
        currentCustomer = (Customer) intent.getSerializableExtra("customer");
        // materialToolbar = findViewById(R.id.materialToolbar);
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

    private List<SportingActivityClass> customerSportingActivitiesList;

    public void func_view_sport_activities() {
        new AsyncTask<Void, Void, List<SportingActivityClass>>() {
            @Override
            protected List<SportingActivityClass> doInBackground(Void... voids) {
                return customer.getSportingActivityClasses();
            }

            @Override
            protected void onPostExecute(List<SportingActivityClass> sportingActivities) {
                customerSportingActivitiesList = customer.getSportingActivityClasses();

                adapter = new SportingActivityAdapter(customer, sportingActivityClassDao, MySportingActivitiesActivity.this, MySportingActivitiesActivity.this);
                adapter.setSportingActivities(sportingActivities);

                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MySportingActivitiesActivity.this));

                SwipeToDelete swipeToDelete = new SwipeToDelete(adapter, app_db, MySportingActivitiesActivity.this);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDelete);
                itemTouchHelper.attachToRecyclerView(recyclerView);

                recyclerView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }
}
package com.example.myapplication;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "customers", indices = {@Index(value = "email", unique = true), @Index(value = "id", unique = true)})
@TypeConverters(Converters.class)
public class Customer implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;


    public String name;
    public String password;
    // public ArrayList<Customer> friends;
    public String location;
    public String email;
    public ArrayList<SportingActivityClass> sportingActivityClasses;


    public Customer(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.initializeSportingActivityClasses();
    }

    public Customer(String value) {
    }

    private void initializeSportingActivityClasses() {
        this.sportingActivityClasses = new ArrayList<>();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

    public void addSportingActivity(SportingActivityClass activity) {
        if (this.sportingActivityClasses == null) {
            this.initializeSportingActivityClasses();
        }
        this.sportingActivityClasses.add(activity);
    }

    @TypeConverter
    public static ArrayList<Customer> fromCustomerListString(String value) {
        Type listType = new TypeToken<ArrayList<Customer>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String toCustomerListString(ArrayList<Customer> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public List<SportingActivityClass> getSportingActivityClasses() {
        return sportingActivityClasses;
    }
}


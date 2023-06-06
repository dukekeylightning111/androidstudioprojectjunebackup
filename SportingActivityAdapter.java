package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Index;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SportingActivityAdapter extends RecyclerView.Adapter<SportingActivityAdapter.ViewHolder> {
    private MediatorLiveData<Map<Index.Order, SportingActivityClass>> liveSportingActivities;
    private List<SportingActivityClass> sportingActivities = new ArrayList<>();
    private Customer customer;
    private SportingActivityClassDao sportingActivityClassDao;
    public Context context;
    public SportingActivityAdapter(MediatorLiveData<Map<Index.Order, SportingActivityClass>> liveSportingActivities, Customer customer, Activity activity) {
        this.liveSportingActivities = liveSportingActivities;
        this.customer = customer;
        this.context = activity;

        liveSportingActivities.observe((LifecycleOwner) activity, new Observer<Map<Index.Order, SportingActivityClass>>() {
            @Override
            public void onChanged(Map<Index.Order, SportingActivityClass> sportingActivitiesMap) {
                List<SportingActivityClass> sportingActivities = new ArrayList<>(sportingActivitiesMap.values());
                setSportingActivities(sportingActivities);
            }
        });
    }


    public SportingActivityAdapter(Customer customer, SportingActivityClassDao sportingActivityClassDao, Activity activity, Context context) {
        this.customer = customer;
        this.sportingActivityClassDao = sportingActivityClassDao;
        this.context = context;
        new LoadSportingActivitiesTask().execute();
    }

    public SportingActivityAdapter(List<SportingActivityClass> sportingActivities, Customer customer) {
        this.customer = customer;
        this.sportingActivities = sportingActivities;
    }

    public void setSportingActivities(List<SportingActivityClass> sportingActivities) {
        this.sportingActivities = sportingActivities;
        notifyDataSetChanged();
    }

    public SportingActivityClass getSportingActivityAtPosition(int position) {
        return sportingActivities.get(position);
    }

    public void removeSportingActivity(int position) {
        sportingActivities.remove(position);
        notifyItemRemoved(position);
    }

    public void deleteSportingActivity(SportingActivityClass sportingActivityClass, SportingActivityClassDao sportingActivityClassDao) {
        sportingActivityClassDao.delete(sportingActivityClass);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sporting_activity_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(sportingActivities.get(position));
    }

    @Override
    public int getItemCount() {
        return sportingActivities.size();
    }

    private class LoadSportingActivitiesTask extends AsyncTask<Void, Void, List<SportingActivityClass>> {
        @Override
        protected List<SportingActivityClass> doInBackground(Void... voids) {
            return sportingActivityClassDao.getSportingActivitiesByUserId(customer.id);
        }

        @Override
        protected void onPostExecute(List<SportingActivityClass> sportingActivities) {
            super.onPostExecute(sportingActivities);
            setSportingActivities(sportingActivities);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView categoryTextView;
        private TextView timeTextView;
        private TextView locationTextView;
        private TextView adminCustomerTextView;
        private TextView activityDescTextView;
        private ImageView imageViewCategory;
        private Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.sport_activity_name);
            timeTextView = itemView.findViewById(R.id.sport_activity_time);
            locationTextView = itemView.findViewById(R.id.sport_activity_location);
            activityDescTextView = itemView.findViewById(R.id.act_desc);
            imageViewCategory = itemView.findViewById(R.id.imageViewCategory);
            context = itemView.getContext();
        }

        public void bind(@NonNull SportingActivityClass sportingActivity) {
            String name = sportingActivity.getTitle();
            String time = sportingActivity.getTime();
            String location = sportingActivity.getLocation();
            locationTextView.setText("מיקום הפעןולה" + location);
            String description = activityDescTextView.getText().toString() + " " + sportingActivity.getDescription();
            Customer customer = sportingActivity.getCustomer();
/*
            if (customer != null && customer.getName() != null) {
                adminCustomerTextView.setText("name:" + customer.getName());
            }*/

            String desc_new_lines = description.replaceAll("\\.", "\n");
            activityDescTextView.setText(desc_new_lines);
            nameTextView.setText(name);
            timeTextView.setText(sportingActivity.getDate() + " " + time);

            activityDescTextView.setText(description);
            imageViewCategory.setScaleType(ImageView.ScaleType.CENTER_CROP);

            String category = sportingActivity.getCategory();
            if (category.equals("football")) {
                imageViewCategory.setImageResource(R.drawable.people_football_image);
            } else if (category.equals("basketball")) {
                imageViewCategory.setImageResource(R.drawable.basketbal_in_activity);
            } else if (category.equals("tennis")) {
                imageViewCategory.setImageResource(R.drawable.tennis_in_activity_wallpaper);
            } else if (category.equals("volleyball")) {
                imageViewCategory.setImageResource(R.drawable.people_volleyval_image);
            }
        }
    }}

package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class AboutMeFragment extends Fragment {
    private Button contactButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_me, container, false);
        contactButton = view.findViewById(R.id.contactBtn);
        func_contactMe();
        return view;
    } public void func_contactMe(){
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_contact_me = new Intent(getActivity(), ContactMeActivity.class);
                startActivity(intent_contact_me);

            }
        });
    }

}
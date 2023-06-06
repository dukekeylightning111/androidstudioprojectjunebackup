package com.example.myapplication;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactMeActivity extends AppCompatActivity {
    private Button contactViaPhone;
    private Customer customer;
    private Button contactViaEmail;
    private BroadcastReceiver broadcastReceiver;
    private EditText editTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_me);
        init();
        func_contact_via_email();
        func_contact_via_phone();
    }

    public void init() {
        Intent intent = getIntent();
        customer = (Customer) intent.getSerializableExtra("customer");
        contactViaEmail = findViewById(R.id.contactViaEmailBtn);
        contactViaPhone = findViewById(R.id.contactViaPhoneBtn);
        editTextMessage = findViewById(R.id.editTextMessage);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    showNoDataConnectionDialog();
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    Toast.makeText(context, "משתמש בחיבור Wi-Fi", Toast.LENGTH_SHORT).show();
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    Toast.makeText(context, "משתמש בחיבור נתונים סלולרי", Toast.LENGTH_SHORT).show();
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void showNoDataConnectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("אין קליטה")
                .setMessage("האם אתם מעוניינים להמשיך?")
                .setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void func_contact_via_email() {
        Intent i = getIntent();
        customer = (Customer) i.getSerializableExtra("customer");
        contactViaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = customer.getEmail();
                String subject = "הודעה משתמש:" + customer.getName() + "לגבי האפליקציה" ;
                String message = editTextMessage.getText().toString().trim();

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + emailAddress));
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, message);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(ContactMeActivity.this, "No email app found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void func_contact_via_phone() {
        contactViaPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "123456789";
                String message = editTextMessage.getText().toString().trim();

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

                if (isConnected) {
                    if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                        Toast.makeText(ContactMeActivity.this, "משתמש בחיבור Wi-Fi", Toast.LENGTH_SHORT).show();
                    } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Toast.makeText(ContactMeActivity.this, "משתמש בחיבור נתונים סלולרי", Toast.LENGTH_SHORT).show();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactMeActivity.this);
                    builder.setTitle("Confirm Message")
                            .setMessage("Are you sure you want to send the following message?\n\n" + message)
                            .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendSMS(phoneNumber, message);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                } else {
                    showNoDataConnectionDialog();
                }
            }
        });
    }


    private void sendSMS(String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(ContactMeActivity.this, "הודעה נשלחה", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(ContactMeActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}

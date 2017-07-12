package com.example.afs.ihome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Intent intent = getIntent();
        String message = intent.getStringExtra("EXTRA_MESSAGE");
        int id = intent.getIntExtra("EXTRA_ID", 0);
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setText("Message " + id + ":\n" + message);
		/*
		NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationActivity.this.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		*/
    }
}


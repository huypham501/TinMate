package com.hcmus.tinuni.Activity.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.hcmus.tinuni.R;

import org.w3c.dom.Text;

public class UserReportActivity extends Activity {

    private TextView target_email;
    private String target;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_report);

        target_email = findViewById(R.id.target_email);

        Intent intent = getIntent();
        target = intent.getStringExtra("target");

        target_email.setText(target);
    }
}
package com.hcmus.tinuni.Activity.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinuni.Model.ReportMessage;
import com.hcmus.tinuni.R;

import org.w3c.dom.Text;

public class UserReportActivity extends Activity {

    private Button send_feedback;
    private TextView user_report_back_btn;
    private EditText crime_description;
    private RadioGroup radioGroup;
    private RadioButton selectedRadioButton;

    private String owner;
    private String target;
    private String crime_tag;
    private String description;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_report);

        Intent intent = getIntent();
        target = intent.getStringExtra("target");

        send_feedback = (Button)findViewById(R.id.send_feedback);
        send_feedback.setVisibility(View.INVISIBLE);
        user_report_back_btn = (TextView) findViewById(R.id.user_report_back_btn);
        crime_description = (EditText)findViewById(R.id.crime_description);
        radioGroup = (RadioGroup)findViewById(R.id.crime_tag_group);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        owner = mUser.getEmail();
        //----------------------------------------------------------------------
        user_report_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserReportActivity.super.onBackPressed();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                send_feedback.setVisibility(View.VISIBLE);

                int selectedId = radioGroup.getCheckedRadioButtonId();
                selectedRadioButton = (RadioButton)findViewById(selectedId);
                crime_tag = (String)selectedRadioButton.getText();
            }
        });

        send_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentMillis = String.valueOf(System.currentTimeMillis());

                description = crime_description.getText().toString();

                ReportMessage reportMessage = new ReportMessage(currentMillis,owner, target, crime_tag, description);
                mDatabase.child("ReportMessages").push().setValue(reportMessage);

                Toast.makeText(UserReportActivity.super.getApplicationContext(), "Thankyou, we've received your feedback", Toast.LENGTH_LONG).show();
                UserReportActivity.super.onBackPressed();
            }
        });
    }
}
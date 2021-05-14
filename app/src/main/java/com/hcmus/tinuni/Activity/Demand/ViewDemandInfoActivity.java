package com.hcmus.tinuni.Activity.Demand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hcmus.tinuni.R;

public class ViewDemandInfoActivity extends Activity {
    private TextView textViewSubject, textViewMajor, textViewSchool, textViewModified, textViewCreated;
    private ImageView imageViewBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_info_demand);

        // READ INTENT
        Intent intent = getIntent();

        // SETUP LAYOUT
        imageViewBack = findViewById(R.id.imageViewBack);
        textViewSubject = findViewById(R.id.textViewSubject);
        textViewMajor = findViewById(R.id.textViewMajor);
        textViewSchool = findViewById(R.id.textViewSchool);
        textViewModified = findViewById(R.id.textViewModified);
        textViewCreated = findViewById(R.id.textViewCreated);

        // SET CLICK
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



        // SET INFO
        textViewSubject.setText("Subject:  " + intent.getStringExtra("demandSubject"));
        textViewMajor.setText("Major:    " + intent.getStringExtra("demandMajor"));
        textViewSchool.setText("School:   " + intent.getStringExtra("demandSchool"));

    }
}

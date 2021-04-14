package com.hcmus.tinuni.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinuni.R;

public class SettingGroupActivity extends Activity {
    private ImageView ivAvatar, btnAdd, btnGoBack;
    private TextView txtName;

    private RelativeLayout layoutMembers, layoutChangeName, layoutPrivate;

    private String groupId;

    private FirebaseUser mUser;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_group);

        ivAvatar = findViewById(R.id.ivAvatar);
        btnAdd = findViewById(R.id.btnAdd);
        btnGoBack = findViewById(R.id.btnGoBack);
        txtName = findViewById(R.id.name);
        layoutMembers = findViewById(R.id.layoutMembers);
        layoutChangeName = findViewById(R.id.layoutChangeName);
        layoutPrivate = findViewById(R.id.layoutPrivate);

        Intent i = getIntent();
        groupId = i.getStringExtra("groupId");

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("Groups");

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingGroupActivity.this.onBackPressed();
            }
        });
    }
}
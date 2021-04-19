package com.hcmus.tinuni.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.Profile.ShowProfileActitivy;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.R;

public class SettingGroupActivity extends Activity {
    private ImageView ivAvatar, btnAdd, btnGoBack;
    private TextView txtName;

    private RelativeLayout layoutMembers, layoutChangeName, layoutLeave, layoutChangeAvatar;

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
        layoutLeave = findViewById(R.id.layoutLeave);
        layoutChangeAvatar = findViewById(R.id.layoutChangeAvatar);

        Intent i = getIntent();
        groupId = i.getStringExtra("groupId");
        Log.e("SettingActivity: ", groupId);
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mRef = FirebaseDatabase.getInstance().getReference("Groups").child(groupId);
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);

                txtName.setText(group.getName());
                Glide.with(SettingGroupActivity.this)
                        .load(group.getImageURL())
                        .into(ivAvatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add = new Intent(SettingGroupActivity.this, AddToGroupActivity.class);
                add.putExtra("groupId", groupId);
                startActivity(add);
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingGroupActivity.this.onBackPressed();
            }
        });

        layoutMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layoutChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layoutChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        layoutLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
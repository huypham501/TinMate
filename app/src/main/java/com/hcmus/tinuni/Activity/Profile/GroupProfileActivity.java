package com.hcmus.tinuni.Activity.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.Authentication.ChangePasswordActivity;
import com.hcmus.tinuni.Activity.Authentication.SignInActivity;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

public class GroupProfileActivity extends Activity {
    private TextView tvName, tvSchool, tvMajor, tvSubject, tvMemCount;
    private ImageView ivAvatar, btnGoBack;
    private String id;

    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);
        //Initial variables
        tvName = findViewById(R.id.tvName);
        tvSchool = findViewById(R.id.tvSchool);
        tvMajor = findViewById(R.id.tvMajor);
        ivAvatar = findViewById(R.id.ivAvatar);
        tvSubject = findViewById(R.id.tvSubject);
        tvMemCount = findViewById(R.id.tvMemCount);

        btnGoBack = findViewById(R.id.btnGoBack);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        mRef = FirebaseDatabase.getInstance().getReference("Groups").child(id);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                long memCount = snapshot.child("Participants").getChildrenCount();
                tvName.setText(group.getName());

                if (group.getImageURL().matches("default")) {
                    ivAvatar.setImageResource(R.drawable.default_group);
                } else {
                    Glide.with(getApplicationContext())
                            .load(group.getImageURL())
                            .into(ivAvatar);
                }
                tvMajor.setText(group.getMajor() == null ? "" : group.getMajor());
                tvSchool.setText(group.getSchool() == null ? "" : group.getSchool());
                tvSubject.setText(group.getSubject() == null ? "" : group.getSubject());
                tvMemCount.setText(memCount+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //GO BACK
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupProfileActivity.super.onBackPressed();
                finish();
            }
        });

    }
}
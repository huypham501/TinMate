package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

public class MessageActivity extends Activity {

    private TextView username;
    private ImageView imageView;

    private FirebaseUser mUser;
    private DatabaseReference mRef;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        imageView = findViewById(R.id.imageView);
        username = findViewById(R.id.username);

        // Toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        i = getIntent();
        String userId = i.getStringExtra("userId");

        mUser = FirebaseAuth
                .getInstance()
                .getCurrentUser();
        mRef = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(userId);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUserName());

                if (user.getImageURL().equals("default")) {
                    imageView.setImageResource(R.drawable.profile_image);
                } else {
                    Glide.with(MessageActivity.this)
                            .load(user.getImageURL())
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
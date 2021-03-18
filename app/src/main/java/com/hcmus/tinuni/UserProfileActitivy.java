package com.hcmus.tinuni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Model.User;

public class UserProfileActitivy extends AppCompatActivity {

    private TextView tvName, tvFullname, tvEmail, tvPhone, tvGender, tvSchool;
    private Button btnEdit, btnChangePassword;
    private String id;

    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //Initial variables
        tvName = findViewById(R.id.tvName);
        tvFullname = findViewById(R.id.tvFullname);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvGender = findViewById(R.id.tvGender);
        tvSchool = findViewById(R.id.tvSchool);
        btnEdit = findViewById(R.id.btnEdit);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        System.out.println(id);
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(id);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                System.out.println(user);
                tvName.setText(user.getUsername().toString());
                tvFullname.setText(user.getUsername().toString());
                tvEmail.setText(user.getEmail().toString());
                tvPhone.setText(user.getPhone().toString());
                tvGender.setText(user.getGender().toString());
                tvSchool.setText(user.getSchool().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_edit = new Intent(UserProfileActitivy.this, EditProfileActivity.class);
                System.out.println("2nd id: " + id);
                intent_edit.putExtra("id", id);
                startActivity(intent_edit);
            }
        });

    }
}
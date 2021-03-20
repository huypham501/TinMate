package com.hcmus.tinuni.Activity.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtFullname, edtEmail, edtPhone, edtSchool;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnEdit, btnGoBack;
    String id;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtSchool = findViewById(R.id.edtSchool);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        btnEdit = findViewById(R.id.btnSave);
        btnGoBack = findViewById(R.id.btnGoBack);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        System.out.println("edit"+ id);
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(id);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                edtFullname.setText(user.getUsername());
                edtEmail.setText(user.getEmail());

                if(user.getPhone() == null) {
                    edtPhone.setText("");
                } else {
                    edtPhone.setText(user.getPhone());
                }

                if(user.getGender() == null) {
                    rbMale.setChecked(false);
                    rbFemale.setChecked(false);
                } else {
                    if(user.getGender().matches("Male")) {
                        rbMale.setChecked(true);
                    } else if(user.getGender().matches("Female")) {
                        rbFemale.setChecked(true);
                    }
                }


                if(user.getSchool() == null) {
                    edtSchool.setText("");
                } else {
                    edtSchool.setText(user.getSchool());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtFullname.getText().toString();
                String email = edtEmail.getText().toString();
                String phone = edtPhone.getText().toString();
                String school = edtSchool.getText().toString();
                String img = "default";

                int gender_index = rgGender.getCheckedRadioButtonId();
                RadioButton rd_gender = findViewById(gender_index);
                String gender = rd_gender.getText().toString();

                if(TextUtils.isEmpty(username)) {
                    edtFullname.setError("Username can't be empty");
                } else if(TextUtils.isEmpty(phone)) {
                    edtPhone.setError("Phone can't be empty");
                } else if(TextUtils.isEmpty(school)) {
                    edtSchool.setError("School can't be empty");
                } else if (rgGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(EditProfileActivity.this, "Gender can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    User new_user = new User(id, username, email, img, phone, gender, school);
                    mRef.setValue(new_user);
                    Toast.makeText(EditProfileActivity.this, "Save successfully !", Toast.LENGTH_SHORT).show();

                    Intent go_back = new Intent(EditProfileActivity.this, UserProfileActitivy.class);
                    go_back.putExtra("id", id);
                    startActivity(go_back);
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_back = new Intent(EditProfileActivity.this, UserProfileActitivy.class);
                go_back.putExtra("id", id);
                startActivity(go_back);
            }
        });

    }
}
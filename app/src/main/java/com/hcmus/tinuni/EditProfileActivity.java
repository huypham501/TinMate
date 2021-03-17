package com.hcmus.tinuni;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtFullname, edtEmail, edtPhone, edtGender, edtSchool;
    private Button btnEdit, btnChangePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtSchool = findViewById(R.id.edtSchool);
        btnEdit = findViewById(R.id.btnSave);
        btnChangePassword = findViewById(R.id.btnGoBack);

    }
}
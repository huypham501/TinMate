package com.hcmus.tinuni.Activity.Authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinuni.Activity.MainActivity;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.Calendar;
import java.util.HashMap;

public class SignUpProcessPersonalActivity extends Activity {
    private TextInputLayout textFieldName, textFieldPhone;
    private ProgressBar progressBar;
    private Button buttonNext;
    private RadioGroup radioGroupGender;
    private RadioButton lastRadioButton;

    private FirebaseUser firebaseCurrUser;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_process_personal);

        textFieldName = findViewById(R.id.textFieldName);
        radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        lastRadioButton = (RadioButton) findViewById(R.id.radioButtonFemale);
        textFieldPhone = findViewById(R.id.textFieldPhone);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidForm()) {
                    progressBar.setVisibility(View.VISIBLE);
                    commitToDatabase();
                }
            }
        });
        firebaseCurrUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private boolean isValidForm() {
        final String strTextFieldName = textFieldName.getEditText().getText().toString();
        final String strTextFieldPhone = textFieldPhone.getEditText().getText().toString();

        textFieldName.setError(null);
        lastRadioButton.setError(null);
        textFieldPhone.setError(null);

        if (strTextFieldName.isEmpty()) {
            textFieldName.setError("Please fill in name!");
        } else if (strTextFieldName.length() < 2 || strTextFieldName.length() > 30) {
            textFieldName.setError("Name should be from 2 - 30 characters");
        } else if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            lastRadioButton.setError("Please choose gender!");
        } else if (strTextFieldPhone.isEmpty()) {
            textFieldPhone.setError("Please fill in phone!");
        } else if (strTextFieldPhone.length() < 10 || strTextFieldPhone.length() > 15) {
            textFieldPhone.setError("Invalid phone!");
        } else {
            return true;
        }
        return false;
    }

    private void commitToDatabase() {
        RadioButton selectedRadioButton = (RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId());
        final String strTextFieldName = textFieldName.getEditText().getText().toString();
        final String strTextFieldPhone = textFieldPhone.getEditText().getText().toString();
        final String strGender = selectedRadioButton.getText().toString();

        HashMap hashMapUserPersonal = new HashMap();
        hashMapUserPersonal.put("username", strTextFieldName);
        hashMapUserPersonal.put("phone", strTextFieldPhone);
        hashMapUserPersonal.put("gender", strGender);

        String userId = firebaseCurrUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        databaseReference.setValue(hashMapUserPersonal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpProcessPersonalActivity.this, "Error commit data", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    moveActivity(SignUpProcessPersonalActivity.this, SignUpProcessLevelActivity.class);
                }
            }
        });
    }

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
        finish();
    }
}

package com.hcmus.tinuni.Activity.Authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinuni.Activity.MainActivity;
import com.hcmus.tinuni.R;

import java.util.Calendar;
import java.util.HashMap;

public class SignUpProcessEducationActivity extends Activity {
    private TextView textViewWelcome;
    private TextInputLayout textFieldSchoolName, textFieldMajor, textFieldYearBegins;
    private ProgressBar progressBar;
    private Button buttonNext, buttonSkip;
    private String userId;

    private FirebaseUser firebaseCurrUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_education);

        textFieldSchoolName = findViewById(R.id.textFieldSchoolName);
        textFieldMajor = findViewById(R.id.textFieldMajors);
        textFieldYearBegins = findViewById(R.id.textFieldSchoolYear);
        textViewWelcome = (TextView) findViewById(R.id.textViewWelcome);

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

        buttonSkip = findViewById(R.id.buttonSkip);
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveActivity(SignUpProcessEducationActivity.this, MainActivity.class);
            }
        });

        firebaseCurrUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseCurrUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpProcessEducationActivity.this, "Error get data user", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("hereeeeeeeeeeeeeeeeeeee");
                    System.out.println(task.getResult().child("userName").getValue().toString());
                    textViewWelcome.setText(String.format("Hello %s,", task.getResult().child("userName").getValue().toString()));
                }
            }
        });
    }

    private boolean isValidForm() {
        final String strTextFieldSchoolName = textFieldSchoolName.getEditText().getText().toString();
        final String strTextFieldMajor = textFieldMajor.getEditText().getText().toString();
        final String strTextFieldYearBegins = textFieldYearBegins.getEditText().getText().toString();

        textFieldSchoolName.setError(null);
        textFieldMajor.setError(null);
        textFieldYearBegins.setError(null);

        if (strTextFieldSchoolName.isEmpty()) {
            textFieldSchoolName.setError("Please fill in school!");
        } else if (strTextFieldMajor.isEmpty()) {
            textFieldMajor.setError("Please fill in major!");
        } else if (strTextFieldYearBegins.isEmpty()) {
            textFieldYearBegins.setError("Please fill in school year!");
        } else if (strTextFieldSchoolName.length() < 2 || strTextFieldSchoolName.length() > 50) {
            textFieldSchoolName.setError("School name should be from 2 - 50 characters");
        } else if (strTextFieldMajor.length() < 2 || strTextFieldMajor.length() > 50) {
            textFieldMajor.setError("Major should be from 2 - 50 characters");
        } else {
            int gapYear = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(strTextFieldYearBegins);
            if (gapYear < 0 || gapYear > 100) {
                textFieldYearBegins.setError("Invalid school year");
            } else {
                return true;
            }
        }
        return false;
    }

    private void commitToDatabase() {
        final String strTextFieldSchoolName = textFieldSchoolName.getEditText().getText().toString();
        final String strTextFieldMajor = textFieldMajor.getEditText().getText().toString();
        final String strTextFieldYearBegins = textFieldYearBegins.getEditText().getText().toString();

        HashMap hashMapUserEducation = new HashMap();
        hashMapUserEducation.put("schoolName", strTextFieldSchoolName);
        hashMapUserEducation.put("major", strTextFieldMajor);
        hashMapUserEducation.put("yearBegins", strTextFieldYearBegins);

        databaseReference.updateChildren(hashMapUserEducation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpProcessEducationActivity.this, "Error commit data", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    moveActivity(SignUpProcessEducationActivity.this, MainActivity.class);
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

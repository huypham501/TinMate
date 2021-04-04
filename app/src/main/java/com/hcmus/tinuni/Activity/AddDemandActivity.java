package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.R;

public class AddDemandActivity extends Activity {
    private EditText editTextSubject, editTextMajor, editTextSchool, editTextLevel;
    private Button buttonSave, buttonBack;
    private ProgressBar progressBar;

    private String strEditTextSubject;
    private String strEditTextMajor;
    private String strEditTextSchool;

    private String userId;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_demand);

        editTextSubject = findViewById(R.id.editTextSubject);
        editTextMajor = findViewById(R.id.editTextMajor);
        editTextSchool = findViewById(R.id.editTextSchool);

        progressBar = findViewById(R.id.progressBar);

        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                initIdUser();

                if (isValidForm()) {
                    Demand demand = new Demand(strEditTextSubject, strEditTextMajor, strEditTextSchool);
                    databaseReference.push().setValue(demand).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(AddDemandActivity.this, "Error commit data", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                AddDemandActivity.super.onBackPressed();
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!progressBar.isAnimating()) {
                    AddDemandActivity.super.onBackPressed();
                }
            }
        });
    }

    private void initIdUser() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Demands").child(userId);
    }

    private boolean isValidForm() {
        strEditTextSubject = editTextSubject.getText().toString();
        if (strEditTextSubject.isEmpty()) {
            editTextSubject.setError("Please fill in subject");
            return false;
        }

        strEditTextMajor = editTextMajor.getText().toString();
        System.out.println(strEditTextMajor);
        if (strEditTextMajor.isEmpty()) {
            editTextMajor.setError("Please fill in major");
            return false;
        }

        strEditTextSchool = editTextSchool.getText().toString();
        if (strEditTextSchool.isEmpty()) {
            editTextSchool.setError("Please fill in school");
            return false;
        }

        if (strEditTextSubject.length() < 2 || strEditTextSubject.length() > 50) {
            editTextSubject.setError("Subject name should be from 2 - 50 characters");
        } else if (strEditTextMajor.length() < 2 || strEditTextMajor.length() > 50) {
            editTextMajor.setError("Major name should be from 2 - 50 characters");
        } else if (strEditTextSchool.length() < 2 || strEditTextSchool.length() > 50) {
            editTextSchool.setError("School name should be from 2 - 50 characters");
        } else {
            return true;
        }
        return false;
    }
}

package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    private String strEditTextLevel;

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
        editTextLevel = findViewById(R.id.editTextLevel);

        progressBar = findViewById(R.id.progressBar);

        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                initIdUser();

                if (isValidForm()) {
                    Demand demand = new Demand(strEditTextSubject, strEditTextMajor, strEditTextSchool, strEditTextLevel);
                    databaseReference.push().setValue(demand).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(AddDemandActivity.this, "Error commit data", Toast.LENGTH_SHORT).show();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
//                                MOVE ACTIVITY HEREEEEEEEEEEEEEEEE
                            }
                        }
                    });
                }
            }
        });

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MOVE ACTIVITY HEREEEEEEEEEEEEEEEEE
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

        strEditTextMajor = editTextLevel.getText().toString();
        if (strEditTextMajor.isEmpty()) {
            editTextMajor.setError("Please fill in major");
            return false;
        }

        strEditTextSchool = editTextSchool.getText().toString();
        if (strEditTextSchool.isEmpty()) {
            editTextSchool.setError("Please fill in school");
            return false;
        }

        strEditTextLevel = editTextLevel.getText().toString();
        if (strEditTextLevel.isEmpty()) {
            editTextLevel.setError("Please fill in level");
            return false;
        }

        if (strEditTextSubject.length() < 3 || strEditTextSubject.length() > 50) {
            editTextSubject.setError("Subject name should be from 2 - 50 characters");
        } else if (strEditTextMajor.length() < 3 || strEditTextMajor.length() > 50) {
            editTextMajor.setError("Major name should be from 2 - 50 characters");
        } else if (strEditTextSchool.length() < 3 || strEditTextSchool.length() > 50) {
            editTextSchool.setError("School name should be from 2 - 50 characters");
        } else if (strEditTextLevel.length() < 3 || strEditTextLevel.length() > 50) {
            editTextLevel.setError("Level name should be from 2 - 50 characters");
        } else {
            return true;
        }
        return false;
    }

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
        finish();
    }

}

package com.hcmus.tinuni.Activity.Authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinuni.Activity.MainActivity;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignUpProcessLevelActivity extends Activity {
    private Spinner spinnerLevel;
    private Button buttonNext;
    private ProgressBar progressBar;
    private FirebaseUser firebaseCurrUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_process_level);

        spinnerLevel = findViewById(R.id.spinnerLevel);
        List<String> listLevel = new ArrayList<>();
        listLevel.add("Your level");
        listLevel.add("High school");
        listLevel.add("College");
        listLevel.add("University");
        listLevel.add("Master’s degree");
        listLevel.add("Other");

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, listLevel) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerLevel.setAdapter(spinnerArrayAdapter);

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
        final int indexLevel = spinnerLevel.getSelectedItemPosition();
        if (indexLevel == 0) {
            TextView textViewError = (TextView) spinnerLevel.getSelectedView();
            textViewError.setTextColor(Color.RED);
            textViewError.setText("Please choose level!");
            return false;
        }
        return true;
    }

    private void commitToDatabase() {
        final String strSpinnerLevel = spinnerLevel.getSelectedItem().toString();
        final int indexSelectedSpinner = spinnerLevel.getSelectedItemPosition();
        HashMap hashMapUsersLevel = new HashMap();
        hashMapUsersLevel.put("level", strSpinnerLevel);

        String userId = firebaseCurrUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        databaseReference.setValue(hashMapUsersLevel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpProcessLevelActivity.this, "Error commit data", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (indexSelectedSpinner == 5) {
                        moveActivity(SignUpProcessLevelActivity.this, MainActivity.class);
                    } else {
                        moveActivity(SignUpProcessLevelActivity.this, SignUpProcessEducationActivity.class);
                    }
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

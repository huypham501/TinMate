package com.hcmus.tinuni;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpProcessActivity extends Activity {
    private TextInputLayout textFieldName, textFieldPhone, textFieldSchoolName, textFieldMajors, textFieldSchoolYear;
    private Button buttonContinue;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_process);

        final Spinner spinnerLevel = findViewById(R.id.spinnerLevel);
        ArrayList<String> arrayListLevel = new ArrayList<>();
        arrayListLevel.add("High school");
        arrayListLevel.add("College");
        arrayListLevel.add("University");
        arrayListLevel.add("Master’s degree");

        final ArrayAdapter<String> arrayAdapterLevel = new ArrayAdapter<String>(this, R.layout.spinner_item) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;

                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor((Color.BLACK));
                }
                return view;
            }
        };

        arrayAdapterLevel.setDropDownViewResource(R.layout.spinner_item);
        spinnerLevel.setAdapter(arrayAdapterLevel);

        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if (position > 0) {
                    // Notify the selected item text
                    Toast.makeText
                            (getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textFieldName = findViewById(R.id.textFieldName);
        textFieldPhone = findViewById(R.id.textFieldPhone);
        textFieldSchoolName = findViewById(R.id.textFieldSchoolName);
        textFieldMajors = findViewById(R.id.textFieldMajors);
        textFieldSchoolYear = findViewById(R.id.textFieldSchoolYear);

        buttonContinue = findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private boolean isValidForm() {
        final String strTextFieldName = textFieldName.getEditText().getText().toString();
        final String strtTxtFieldPhone = textFieldPhone.getEditText().getText().toString();
        final String strTextFieldSchoolName = textFieldSchoolName.getEditText().getText().toString();
        final String strTextFieldMajors = textFieldMajors.getEditText().getText().toString();
        final String strTextFieldSchoolYear = textFieldSchoolYear.getEditText().getText().toString();


        return false;
    }

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
        finish();
    }
}

package com.hcmus.tinuni.Activity.Authentication;

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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class SignUpProcessActivity extends Activity {
    private TextInputLayout textFieldName, textFieldPhone, textFieldSchoolName, textFieldMajors, textFieldSchoolYear;
    private Spinner spinnerLevel;
    private ProgressBar progressBar;
    private Button buttonContinue;
    private RadioGroup radioGroupGender;
    private RadioButton lastRadioButton;

    private FirebaseUser firebaseCurrUser;
    //private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_process);

        textFieldName = findViewById(R.id.textFieldName);
        radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        lastRadioButton = (RadioButton) findViewById(R.id.radioButtonFemale);
        textFieldPhone = findViewById(R.id.textFieldPhone);
        textFieldSchoolName = findViewById(R.id.textFieldSchoolName);
        textFieldMajors = findViewById(R.id.textFieldMajors);

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

        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItemText = (String) adapterView.getItemAtPosition(position);
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
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textFieldSchoolYear = findViewById(R.id.textFieldSchoolYear);
        buttonContinue = findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidForm()) {
                    progressBar.setVisibility(View.VISIBLE);

                }
            }
        });
        firebaseCurrUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private boolean isValidForm() {
        final String strTextFieldName = textFieldName.getEditText().getText().toString();
        final String strTextFieldPhone = textFieldPhone.getEditText().getText().toString();
        final String strTextFieldSchoolName = textFieldSchoolName.getEditText().getText().toString();
        final String strTextFieldMajors = textFieldMajors.getEditText().getText().toString();
        final int indexLevel = spinnerLevel.getSelectedItemPosition();
        final String strTextFieldSchoolYear = textFieldSchoolYear.getEditText().getText().toString();
        int gapYear;

        textFieldName.setError(null);
        lastRadioButton.setError(null);
        textFieldPhone.setError(null);
        textFieldSchoolName.setError(null);
        textFieldMajors.setError(null);
        textFieldSchoolYear.setError(null);

        if (strTextFieldName.isEmpty()) {
            textFieldName.setError("Please fill in name!");
        } else if (radioGroupGender.getCheckedRadioButtonId() == -1) {
            lastRadioButton.setError("Please choose gender!");
        } else if (strTextFieldPhone.isEmpty()) {
            textFieldPhone.setError("Please fill in phone!");
        } else if (strTextFieldSchoolName.isEmpty()) {
            textFieldSchoolName.setError("Please fill in school!");
        } else if (strTextFieldMajors.isEmpty()) {
            textFieldMajors.setError("Please fill in major!");
        } else if (indexLevel == 0) {
            TextView textViewError = (TextView) spinnerLevel.getSelectedView();
            textViewError.setText("Error");
            textViewError.setTextColor(Color.RED);
            textViewError.setText("Please choose level!");
        } else if (strTextFieldSchoolYear.isEmpty()) {
            textFieldSchoolYear.setError("Please fill in school year!");
        } else if (strTextFieldName.length() < 4) {
            textFieldName.setError("Name should be at least 4 characters");
        } else if (strTextFieldName.length() > 30) {
            textFieldName.setError("Name can't longer than 30 characters");
        } else if (strTextFieldPhone.length() < 10 || strTextFieldPhone.length() > 15) {
            textFieldPhone.setError("Invalid phone!");
        } else if (strTextFieldSchoolName.length() < 4) {
            textFieldSchoolName.setError("Name should be at least 4 characters");
        } else if (strTextFieldSchoolName.length() > 50) {
            textFieldSchoolName.setError("Name can't longer than 50 characters");
        } else if (strTextFieldMajors.length() < 4) {
            textFieldMajors.setError("Major should be at least 4 characters");
        } else if (strTextFieldMajors.length() > 50) {
            textFieldMajors.setError("Major can't longer than 50 characters");
        } else {
            gapYear = Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(strTextFieldSchoolYear);
            if (gapYear < 0 || gapYear > 100) {
                textFieldSchoolYear.setError("Invalid school year");
            } else {
                return true;
            }
        }
        return false;
    }

    private void commitToDadabase() {
        final String strTextFieldName = textFieldName.getEditText().getText().toString();
        final String strTextFieldPhone = textFieldPhone.getEditText().getText().toString();
        final String strTextFieldSchoolName = textFieldSchoolName.getEditText().getText().toString();
        final String strTextFieldMajors = textFieldMajors.getEditText().getText().toString();
        final String strLevel = spinnerLevel.getSelectedItem().toString();
        final int intSchoolYear = Integer.parseInt(textFieldSchoolYear.getEditText().getText().toString());

        RadioButton selectedRadioButton = (RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId());

        final String strGender = selectedRadioButton.getText().toString();

        String userId = firebaseCurrUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        databaseReference.setValue(new User(userId, strTextFieldName, strTextFieldPhone, strTextFieldSchoolName, strTextFieldMajors, intSchoolYear, strGender)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(SignUpProcessActivity.this, "Error commit data", Toast.LENGTH_SHORT).show();
                } else
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    moveActivity(SignUpProcessActivity.this, MainActivity.class);
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

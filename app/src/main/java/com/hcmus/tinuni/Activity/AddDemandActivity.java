package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.R;

public class AddDemandActivity extends Activity {
    private EditText editTextSubject, editTextMajor, editTextSchool, editTextLevel;
    private Button buttonSave;
    private TextView textViewDuplicateDemandWarning;
    private ImageView btnGoBack;
    private AlertDialog alertDialog;

    private String strEditTextSubject;
    private String strEditTextMajor;
    private String strEditTextSchool;

    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_demand);

        editTextSubject = findViewById(R.id.editTextSubject);
        editTextMajor = findViewById(R.id.editTextMajor);
        editTextSchool = findViewById(R.id.editTextSchool);

        textViewDuplicateDemandWarning = findViewById(R.id.textViewDuplicateDemandWarning);

        AlertDialog.Builder builder = new AlertDialog.Builder(AddDemandActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.layout_loading_dialog);
        alertDialog = builder.create();

        buttonSave = findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();

                initIdUser();

                if (isValidForm()) {
                    Demand demand = new Demand(strEditTextSubject, strEditTextMajor, strEditTextSchool);

                    Query query = FirebaseDatabase.getInstance().getReference("Demands").orderByChild("userId").equalTo(userId);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean isDuplicate = false;

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Demand demandTemp = dataSnapshot.getValue(Demand.class);

                                if (demandTemp.isEqual(demand)) {
                                    isDuplicate = true;
                                    break;
                                }
                            }

                            if (isDuplicate) {
                                alertDialog.dismiss();
                                textViewDuplicateDemandWarning.setVisibility(View.VISIBLE);
                            } else {
                                pushData(demand);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    alertDialog.dismiss();
                }
            }
        });

        btnGoBack = findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!alertDialog.isShowing()) {
                    AddDemandActivity.super.onBackPressed();
                }
            }
        });
    }

    private void pushData(Demand demand) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Demands");

        String demandKey = databaseReference.push().getKey();
        databaseReference.child(demandKey).setValue(demand).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(AddDemandActivity.this, "Error commit data", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                } else {
                    databaseReference.child(demandKey).child("userId").setValue(userId).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(AddDemandActivity.this, "Error commit data", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            } else {
                                alertDialog.dismiss();
                                databaseReference.child(demandKey).child("id").setValue(demandKey).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(AddDemandActivity.this, "Error commit data", Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                        } else {
                                            alertDialog.dismiss();
                                            AddDemandActivity.super.onBackPressed();
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });
    }

    private void initIdUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = firebaseUser.getUid();
    }

    private boolean isValidForm() {
        strEditTextSubject = editTextSubject.getText().toString();
        if (strEditTextSubject.isEmpty()) {
            editTextSubject.setError("Please fill in subject");
            return false;
        }

        strEditTextMajor = editTextMajor.getText().toString();
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

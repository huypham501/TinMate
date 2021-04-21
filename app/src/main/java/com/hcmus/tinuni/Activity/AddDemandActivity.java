package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.hcmus.tinuni.Adapter.AutoCompleteAdapter;
import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

public class AddDemandActivity extends Activity {
    private AutoCompleteTextView autoCompleteTextViewMajor, autoCompleteTextViewSchool, autoCompleteTextViewSubject;

    private Button buttonSave;
    private TextView textViewDuplicateDemandWarning;
    private ImageView btnGoBack;
    private AlertDialog alertDialog;

    private String strEditTextSubject;
    private String strEditTextMajor;
    private String strEditTextSchool;

    private String userId;

    List<String> arrayListSuggestMajor = new ArrayList<>();
    List<String> arrayListSuggestSubject = new ArrayList<>();
    List<String> arrayListSuggestSchool = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_demand);

        autoCompleteTextViewSubject = findViewById(R.id.autoCompleteTextViewSubject);
        autoCompleteTextViewMajor = findViewById(R.id.autoCompleteTextViewMajor);
        autoCompleteTextViewSchool = findViewById(R.id.autoCompleteTextViewSchool);

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

        // LOAD SUGGEST
        DatabaseReference databaseReferenceSuggestMajor = FirebaseDatabase.getInstance().getReference("Menu").child("major");
        databaseReferenceSuggestMajor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {

                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        arrayListSuggestMajor.add(dataSnapshot.getValue().toString());
                    }
                    AutoCompleteAdapter adapter = new AutoCompleteAdapter(AddDemandActivity.this, arrayListSuggestMajor);
                    autoCompleteTextViewMajor.setAdapter(adapter);
                    System.out.println("DONE MAJOR");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReferenceSuggestSubject = FirebaseDatabase.getInstance().getReference("Menu").child("subject");
        databaseReferenceSuggestSubject.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {

                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        arrayListSuggestSubject.add(dataSnapshot.getValue().toString());
                    }
                    AutoCompleteAdapter adapter = new AutoCompleteAdapter(AddDemandActivity.this, arrayListSuggestSubject);
                    autoCompleteTextViewSubject.setAdapter(adapter);
                    System.out.println("DONE SUBJECT");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReferenceSuggestSchool = FirebaseDatabase.getInstance().getReference("Menu").child("school");
        databaseReferenceSuggestSchool.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {

                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        arrayListSuggestSchool.add(dataSnapshot.getValue().toString());
                    }
                    AutoCompleteAdapter adapter = new AutoCompleteAdapter(AddDemandActivity.this, arrayListSuggestSchool);
                    autoCompleteTextViewSchool.setAdapter(adapter);
                    System.out.println("DONE SCHOOL");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        strEditTextSubject = autoCompleteTextViewSubject.getText().toString();
        if (strEditTextSubject.isEmpty()) {
            autoCompleteTextViewSubject.setError("Please fill in subject");
            return false;
        }

        strEditTextMajor = autoCompleteTextViewMajor.getText().toString();
        if (strEditTextMajor.isEmpty()) {
            autoCompleteTextViewMajor.setError("Please fill in major");
            return false;
        }

        strEditTextSchool = autoCompleteTextViewSchool.getText().toString();
        if (strEditTextSchool.isEmpty()) {
            autoCompleteTextViewSchool.setError("Please fill in school");
            return false;
        }

        if (strEditTextSubject.length() < 2 || strEditTextSubject.length() > 50) {
            autoCompleteTextViewSubject.setError("Subject name should be from 2 - 50 characters");
        } else if (strEditTextMajor.length() < 2 || strEditTextMajor.length() > 50) {
            autoCompleteTextViewMajor.setError("Major name should be from 2 - 50 characters");
        } else if (strEditTextSchool.length() < 2 || strEditTextSchool.length() > 50) {
            autoCompleteTextViewSchool.setError("School name should be from 2 - 50 characters");
        } else {
            return true;
        }
        return false;
    }
}

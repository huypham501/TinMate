package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.hcmus.tinuni.Adapter.AutoCompleteAdapter;
import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGroupDemandActivity extends Activity {
    private EditText editTextGroupName;

    private AutoCompleteTextView autoCompleteTextViewMajor,  autoCompleteTextViewSchool, autoCompleteTextViewSubject;

    private Button buttonSave;
    private TextView textViewDuplicateGroupNameWarning;
    private ImageView btnGoBack;
    private AlertDialog alertDialog;

    private String strEditTextGroupName, strEditTextSubject, strEditTextMajor, strEditTextSchool;

    private String userId;

    ArrayList<String> arrayListSuggestMajor = new ArrayList<>();
    List<String> arrayListSuggestSubject = new ArrayList<>();
    List<String> arrayListSuggestSchool = new ArrayList<>();

    final String DEFAULT_URL = "https://firebasestorage.googleapis.com/v0/b/tinuni.appspot.com/o/images%2Favatars%2Fdefault_group.png?alt=media&token=ac09066b-0948-4d70-a73d-ea96e3967470";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_demand);

        //FIELD SETUP
        editTextGroupName = findViewById(R.id.editTextGroupName);
        autoCompleteTextViewSubject = findViewById(R.id.autoCompleteTextViewSubject);
        autoCompleteTextViewMajor = findViewById(R.id.autoCompleteTextViewMajor);
        autoCompleteTextViewSchool = findViewById(R.id.autoCompleteTextViewSchool);

        textViewDuplicateGroupNameWarning = findViewById(R.id.textViewDuplicateGroupNameWarning);

        //ALERT SETUP
        AlertDialog.Builder builder = new AlertDialog.Builder(AddGroupDemandActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.layout_loading_dialog);
        alertDialog = builder.create();

        //BUTTON SAVE SETUP
        buttonSave = findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();

                initIdUser();

                if (isValidForm()) {
                    Demand demand = new Demand(strEditTextSubject, strEditTextMajor, strEditTextSchool);

                    Query query = FirebaseDatabase.getInstance().getReference("Groups").orderByChild("name").equalTo(strEditTextGroupName);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                pushData(demand);
                            } else {
                                alertDialog.dismiss();
                                textViewDuplicateGroupNameWarning.setVisibility(View.VISIBLE);
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
                    AddGroupDemandActivity.super.onBackPressed();
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
                    AutoCompleteAdapter adapter = new AutoCompleteAdapter(AddGroupDemandActivity.this, arrayListSuggestMajor);
                    autoCompleteTextViewMajor.setAdapter(adapter);
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
                    AutoCompleteAdapter adapter = new AutoCompleteAdapter(AddGroupDemandActivity.this, arrayListSuggestSubject);
                    autoCompleteTextViewSubject.setAdapter(adapter);
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
                    AutoCompleteAdapter adapter = new AutoCompleteAdapter(AddGroupDemandActivity.this, arrayListSuggestSchool);
                    autoCompleteTextViewSchool.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void pushData(Demand demand) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Groups");

        String key = databaseReference.push().getKey();
        Group group = new Group(key, strEditTextGroupName, DEFAULT_URL, strEditTextSubject, strEditTextMajor, strEditTextSchool);

        databaseReference.child(key).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(AddGroupDemandActivity.this, "Error commit data", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                } else {
                    // Create Participants
                    Map<String, String> map = new HashMap<>();
                    map.put("id", userId);
                    map.put("role", "owner");
                    databaseReference.child(key).child("Participants").child(userId).setValue(map);

                    // Create ChatList
                    final DatabaseReference chatListRef = FirebaseDatabase.getInstance()
                            .getReference("ChatList")
                            .child(userId)
                            .child(key);

                    chatListRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                chatListRef.child("id").setValue(key);
                            }
                            alertDialog.dismiss();
                            AddGroupDemandActivity.super.onBackPressed();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

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
        strEditTextGroupName = editTextGroupName.getText().toString();
        if (strEditTextGroupName.isEmpty()) {
            editTextGroupName.setError("Please fill in group name");
            return false;
        }

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

        if (strEditTextGroupName.length() < 2 || strEditTextGroupName.length() > 150) {
            editTextGroupName.setError("Group name should be from 2 - 150 characters");
        } else if (strEditTextSubject.length() < 2 || strEditTextSubject.length() > 150) {
            autoCompleteTextViewSubject.setError("Subject name should be from 2 - 150 characters");
        } else if (strEditTextMajor.length() < 2 || strEditTextMajor.length() > 150) {
            autoCompleteTextViewMajor.setError("Major name should be from 2 - 150 characters");
        } else if (strEditTextSchool.length() < 2 || strEditTextSchool.length() > 150) {
            autoCompleteTextViewSchool.setError("School name should be from 2 - 150 characters");
        } else {
            return true;
        }
        return false;
    }

}

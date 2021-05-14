package com.hcmus.tinuni.Activity.Demand;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.R;

public class EditDemandActivity extends AddDemandActivity {
    private TextView textViewSuccess;
    private String demandId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OVERRIDE
        textViewSuccess = findViewById(R.id.textViewSuccess);
        TextView textViewLogoName = findViewById(R.id.logoName);
        textViewLogoName.setText("Edit Demand");
        buttonSave.setText("Save");

        // GET AND SETUP INTENT GROUP INFO
        Intent intent = getIntent();
        demandId = intent.getStringExtra("demandId");
        userId = intent.getStringExtra("userId");
        autoCompleteTextViewSubject.setText(intent.getStringExtra("demandSubject"));
        autoCompleteTextViewMajor.setText(intent.getStringExtra("demandMajor"));
        autoCompleteTextViewSchool.setText(intent.getStringExtra("demandSchool"));

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();

                if (isValidForm()) {
                    Demand demand = new Demand(strEditTextSubject, strEditTextMajor, strEditTextSchool, demandId, userId);

                    Query query = FirebaseDatabase.getInstance().getReference("Demands").orderByChild("userId").equalTo(userId);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean isDuplicate = false;

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Demand demandTemp = dataSnapshot.getValue(Demand.class);

                                if (demandTemp.isEqual(demand, true)) {
                                    isDuplicate = true;
                                    break;
                                }
                            }

                            if (isDuplicate) {
                                alertDialog.dismiss();

                                // VISIBLE WARNING
                                textViewSuccess.setVisibility(View.GONE);
                                textViewDuplicateDemandWarning.setVisibility(View.VISIBLE);
                            } else {
                                updateDemand(demand);
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
    }

    private void updateDemand(Demand demand) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Demands").child(demandId);
        databaseReference.updateChildren(demand.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    alertDialog.dismiss();

                    // VISIBLE SUCCESS
                    textViewDuplicateDemandWarning.setVisibility(View.GONE);
                    textViewSuccess.setVisibility(View.VISIBLE);
                }
            }
        });
    }



}

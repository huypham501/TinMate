package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.Demand.AddDemandActivity;
import com.hcmus.tinuni.Adapter.DemandAdapter;
import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class DemandManageActivity extends Activity {
    private RecyclerView recyclerView;
    private DemandAdapter demandAdapter;

    private ImageView imageViewButtonAdd;
    private TextView textViewDoNotHaveDemandManage;
    private ImageView imageViewBack;

    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetDialog bottomSheetDialogDemandItem;

    private Query querySetupDemandList;
    private ValueEventListener valueEventListenerSetupDemandList;

    public DemandManageActivity() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demand_manage);

        recyclerView = findViewById(R.id.recyclerViewDemandList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        textViewDoNotHaveDemandManage = findViewById(R.id.textViewDoNotHaveDemandManage);

        //BOTTOM SHEET SETUP
        bottomSheetDialog = new BottomSheetDialog(DemandManageActivity.this);
        bottomSheetDialogDemandItem = new BottomSheetDialog(DemandManageActivity.this);


        View viewSheet = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_demand_modal_bottom_sheet, (ViewGroup)findViewById(R.id.linearLayoutBottomSheet));

        viewSheet.findViewById(R.id.linearLayoutCreateGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveActivity(DemandManageActivity.this, AddGroupDemandActivity.class);
            }
        });

        viewSheet.findViewById(R.id.linearLayoutAddDemand).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveActivity(DemandManageActivity.this, AddDemandActivity.class);
            }
        });

        bottomSheetDialog.setContentView(viewSheet);

        //BUTTON ADD SETUP
        imageViewButtonAdd = findViewById(R.id.imageViewButtonAdd);
        imageViewButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.show();
            }
        });

        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setupDemandList();
    }


    @Override
    protected void onStart() {
        super.onStart();
        querySetupDemandList.addValueEventListener(valueEventListenerSetupDemandList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        bottomSheetDialog.dismiss();
        bottomSheetDialogDemandItem.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        querySetupDemandList.removeEventListener(valueEventListenerSetupDemandList);
    }

    private void setupDemandList() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        querySetupDemandList = FirebaseDatabase.getInstance().getReference("Demands").orderByChild("userId").equalTo(userId);

        valueEventListenerSetupDemandList = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Demand> demandArrayList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Demand demand = dataSnapshot.getValue(Demand.class);
                    demandArrayList.add(demand);
                }

                if (demandArrayList.isEmpty()) {
                    textViewDoNotHaveDemandManage.setVisibility(View.VISIBLE);

                    recyclerView.setAdapter(null);
                } else {
                    demandAdapter = new DemandAdapter(DemandManageActivity.this, demandArrayList, userId, bottomSheetDialogDemandItem);
                    recyclerView.setAdapter(demandAdapter);

                    textViewDoNotHaveDemandManage.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
    }
}

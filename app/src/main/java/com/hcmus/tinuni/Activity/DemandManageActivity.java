package com.hcmus.tinuni.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.hcmus.tinuni.Adapter.DemandAdapter;
import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class DemandManageActivity extends Activity {
    private RecyclerView recyclerView;
    private DemandAdapter demandAdapter;

    private ImageButton imageButtonEdit, imageButtonSave;
    private ImageView imageViewButtonAdd;
    private TextView textViewDoNotHaveDemandManage;
    private ImageView imageViewBack;

    private BottomSheetDialog bottomSheetDialog;

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

        View viewSheet = LayoutInflater.from(getApplicationContext()).inflate(R.layout.add_demand_modal_bottom_sheet, (ViewGroup)findViewById(R.id.linearLayoutBottomSheet));

        viewSheet.findViewById(R.id.linearLayoutCreateGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

        imageButtonEdit = findViewById(R.id.imageButtonEdit);
        imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setVisibleAllButton(false);

                imageButtonSave.setVisibility(View.VISIBLE);

                setVisibleAdapterItem(true);
            }
        });

        imageButtonSave = findViewById(R.id.imageButtonSave);
        imageButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(DemandManageActivity.this).setCancelable(false).setView(R.layout.layout_loading_dialog).create();

                demandAdapter.deleteCommit();

                setVisibleAllButton(true);

                imageButtonSave.setVisibility(View.INVISIBLE);

                setVisibleAdapterItem(false);

                alertDialog.dismiss();
            }
        });

        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setDemand();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomSheetDialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bottomSheetDialog.dismiss();
    }

    private void setVisibleAdapterItem(boolean value) {
        demandAdapter.updateVisibility(value);
        demandAdapter.notifyDataSetChanged();
    }

    private void setVisibleAllButton(boolean value) {
        int value_int;
        if (value) {
            value_int = 0; // VISIBLE
        } else {
            value_int = 4; // INVISIBLE
        }
        imageViewButtonAdd.setVisibility(value_int);
        imageButtonEdit.setVisibility(value_int);
    }

    private void setDemand() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query query = FirebaseDatabase.getInstance().getReference("Demands").orderByChild("userId").equalTo(userId);

        ArrayList<Demand> demandArrayList = new ArrayList<>();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                demandArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Demand demand = dataSnapshot.getValue(Demand.class);
                    demandArrayList.add(demand);
                }

                if (demandArrayList.isEmpty()) {
                    textViewDoNotHaveDemandManage.setVisibility(View.VISIBLE);
                    imageButtonEdit.setVisibility(View.INVISIBLE);

                    recyclerView.setAdapter(null);
                } else {
                    demandAdapter = new DemandAdapter(DemandManageActivity.this, demandArrayList);
                    recyclerView.setAdapter(demandAdapter);

                    textViewDoNotHaveDemandManage.setVisibility(View.INVISIBLE);
                    imageButtonEdit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void moveActivity(Context from, Class<?> to) {
        Intent intent = new Intent(from, to);
        startActivity(intent);
    }
}

package com.hcmus.tinuni.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.AddDemandActivity;
import com.hcmus.tinuni.Activity.MainActivity;
import com.hcmus.tinuni.Adapter.DemandAdapter;
import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class DemandManageFragment extends Fragment {
    private RecyclerView recyclerView;
    private DemandAdapter demandAdapter;
    private ArrayList<Demand> demandArrayList;

    private ImageButton imageButtonAdd, imageButtonUpdate, imageButtonNotSave, imageButtonEdit, imageButtonSave;
    private TextView textViewContentDemandManage;

    public DemandManageFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demand_manage, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDemandList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        demandArrayList = new ArrayList<>();

        textViewContentDemandManage = view.findViewById(R.id.textViewContentDemandManage);

        getDemand();

        imageButtonAdd = view.findViewById(R.id.imageButtonAdd);
        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveActivity(getActivity(), AddDemandActivity.class);
            }
        });

        imageButtonNotSave = view.findViewById(R.id.imageButtonNotSave);
        imageButtonNotSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibleAllButton(true);

                imageButtonNotSave.setVisibility(View.INVISIBLE);
                imageButtonSave.setVisibility(View.INVISIBLE);

                setVisibleAdapterItem(false);
            }
        });

        imageButtonEdit = view.findViewById(R.id.imageButtonEdit);
        imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setVisibleAllButton(false);

                imageButtonNotSave.setVisibility(View.VISIBLE);
                imageButtonSave.setVisibility(View.VISIBLE);

                setVisibleAdapterItem(true);
            }
        });

        imageButtonSave = view.findViewById(R.id.imageButtonSave);
        imageButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setVisibleAllButton(true);

                imageButtonNotSave.setVisibility(View.INVISIBLE);
                imageButtonSave.setVisibility(View.INVISIBLE);

                setVisibleAdapterItem(false);
            }
        });

        return view;
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
        imageButtonAdd.setVisibility(value_int);
        imageButtonUpdate.setVisibility(value_int);
        imageButtonEdit.setVisibility(value_int);
    }

    private void getDemand() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Demands").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                demandArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Demand demand = dataSnapshot.getValue(Demand.class);
                    demandArrayList.add(demand);
                }
                demandAdapter = new DemandAdapter(getContext(), demandArrayList);
                recyclerView.setAdapter(demandAdapter);
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

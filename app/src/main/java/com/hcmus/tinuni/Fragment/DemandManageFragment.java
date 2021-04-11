package com.hcmus.tinuni.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hcmus.tinuni.Adapter.DemandAdapter;
import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class DemandManageFragment extends Fragment {
    private RecyclerView recyclerView;
    private DemandAdapter demandAdapter;

    private ImageButton imageButtonAdd, imageButtonEdit, imageButtonSave;
    private TextView textViewDoNotHaveDemandManage;

    public DemandManageFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demand_manage, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewDemandList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        textViewDoNotHaveDemandManage = view.findViewById(R.id.textViewDoNotHaveDemandManage);

        imageButtonAdd = view.findViewById(R.id.imageButtonAdd);
        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveActivity(getActivity(), AddDemandActivity.class);
            }
        });

        imageButtonEdit = view.findViewById(R.id.imageButtonEdit);
        imageButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setVisibleAllButton(false);

                imageButtonSave.setVisibility(View.VISIBLE);

                setVisibleAdapterItem(true);
            }
        });

        imageButtonSave = view.findViewById(R.id.imageButtonSave);
        imageButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setCancelable(false).setView(R.layout.layout_loading_dialog).create();

                demandAdapter.deleteCommit();

                setVisibleAllButton(true);

                imageButtonSave.setVisibility(View.INVISIBLE);

                setVisibleAdapterItem(false);

                alertDialog.dismiss();
            }
        });

        setDemand();

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
        imageButtonEdit.setVisibility(value_int);
    }

    private void setDemand() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference databaseReferenceUserDemand = FirebaseDatabase.getInstance().getReference("Demands").child(userId);

        ArrayList<Demand> demandArrayList = new ArrayList<>();

        databaseReferenceUserDemand.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String demandId = dataSnapshot.getKey();

                    DatabaseReference databaseReferenceDemandsInfo = FirebaseDatabase.getInstance().getReference("DemandsInfo").child(demandId);

                    databaseReferenceDemandsInfo.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isComplete()) {
                                Toast.makeText(getContext(), "Error get data demands info - setdemand() - demand manage fragment", Toast.LENGTH_SHORT).show();
                            } else {
                                String strSubject = task.getResult().child("subject").getValue(String.class);
                                String strMajor = task.getResult().child("major").getValue(String.class);
                                String strSchool = task.getResult().child("school").getValue(String.class);

                                Demand demand = new Demand(strSubject, strMajor, strSchool);
                                demandArrayList.add(new Demand(demand, snapshot.getKey()));

                                demandAdapter = new DemandAdapter(getContext(), demandArrayList);
                                recyclerView.setAdapter(demandAdapter);

                                textViewDoNotHaveDemandManage.setVisibility(View.INVISIBLE);
                                imageButtonEdit.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
                if (demandArrayList.isEmpty()) {
                    textViewDoNotHaveDemandManage.setVisibility(View.VISIBLE);
                    imageButtonEdit.setVisibility(View.INVISIBLE);
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

package com.hcmus.tinuni.Fragment;

import android.app.AlertDialog;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

    private Button buttonTest;

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

        buttonTest = view.findViewById(R.id.buttonTest);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Demands");
                Query query = databaseReference.orderByChild("subject").equalTo("Math");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            System.out.println("HEREEEEEE");
                            System.out.println("not exists");
                        } else {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                System.out.println(dataSnapshot.getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                    demandAdapter = new DemandAdapter(getContext(), demandArrayList);
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

package com.hcmus.tinuni.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class DemandAdapter extends RecyclerView.Adapter<DemandAdapter.ViewHolder> {
    private Context context;
    private View viewSheet;
    private BottomSheetDialog bottomSheetDialog;

    private ArrayList<Demand> demandArrayList;
    private String userId;

    public DemandAdapter(Context context, ArrayList<Demand> demandArrayList, String userId, BottomSheetDialog bottomSheetDialog) {
        this.context = context;
        this.demandArrayList = demandArrayList;
        this.userId = userId;
        this.bottomSheetDialog = bottomSheetDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.demand_item, parent, false);
        viewSheet = LayoutInflater.from(context).inflate(R.layout.view_demand_item_bottom_sheet, null);

        return new DemandAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Demand demand = demandArrayList.get(position);
        holder.textViewSubject.setText(demand.getSubject());
        holder.textViewMajor.setText(demand.getMajor());



        // SETUP CLICK ON ITEM DEMAND
        holder.imageViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SETUP DELETE DEMAND
                viewSheet.findViewById(R.id.linearLayoutDeleteDemand).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(context)
                                .setTitle("Delete demand\nSubject " + demand.getSubject() + "\nMajor " + demand.getMajor() + "\nSchool " + demand.getSchool())
                                .setMessage("Are you sure ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteDemand(userId, demand.getId());

                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        bottomSheetDialog.dismiss();
                                    }
                                }).show();
                    }
                });

                // SETUP EDIT DEMAND
                viewSheet.findViewById(R.id.linearLayoutEditDemand).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                bottomSheetDialog.setContentView(viewSheet);
                bottomSheetDialog.show();
            }
        });

    }

    public void deleteDemand(String userId, String demandId) {
        Query query = FirebaseDatabase.getInstance().getReference("Demands").orderByChild("userId").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (demandId.equals(dataSnapshot.getKey())) {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Demands");
                            databaseReference.child(demandId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {

                                    } else {
                                        // Close bottom dialog when success
                                        bottomSheetDialog.dismiss();
                                        System.out.println("Deleted demand done");
                                    }
                                }
                            });
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return demandArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSubject, textViewMajor;
        private ImageView imageViewMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewSubject = itemView.findViewById(R.id.textViewSubject);
            textViewMajor = itemView.findViewById(R.id.textViewMajor);
            imageViewMore = itemView.findViewById(R.id.imageViewMore);
        }
    }
}


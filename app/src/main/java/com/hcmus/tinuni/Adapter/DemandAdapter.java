package com.hcmus.tinuni.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
    private ArrayList<Demand> demands;
    private boolean isEditorVisible;
    private ArrayList<String> arrayListDemandId = new ArrayList<>();


    public DemandAdapter(Context context, ArrayList<Demand> demands) {
        this.context = context;
        this.demands = demands;
        isEditorVisible = false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.demand_item, parent, false);
        return new DemandAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Demand demand = demands.get(position);
        holder.textViewSubject.setText(demand.getSubject());
        holder.textViewMajor.setText(demand.getMajor());

        if (isEditorVisible) {
            holder.buttonDelete.setVisibility(View.VISIBLE);

            holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete demand")
                            .setMessage("Are you sure")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String demandId = demand.getId();
                                    arrayListDemandId.add(demandId);

                                    holder.itemView.setBackgroundColor(Color.LTGRAY);
                                    holder.buttonRedo.setVisibility(View.VISIBLE);
                                    holder.buttonDelete.setVisibility(View.INVISIBLE);

                                    holder.buttonRedo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            arrayListDemandId.remove(demandId);

                                            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                                            holder.buttonRedo.setVisibility(View.INVISIBLE);
                                            holder.buttonDelete.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            }).setNegativeButton("No", null).show();
                }
            });
        } else {
            holder.buttonDelete.setVisibility(View.INVISIBLE);
        }
    }

    public void deleteCommit() {
        if (arrayListDemandId.isEmpty()) {
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for (String demandId : arrayListDemandId) {

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
                                            System.out.println("Delete demand done");
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

        arrayListDemandId.clear();
    }

    public void updateVisibility(boolean value) {
        isEditorVisible = value;
    }

    @Override
    public int getItemCount() {
        return demands.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewSubject, textViewMajor;
        private Button buttonDelete, buttonRedo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewSubject = itemView.findViewById(R.id.textViewSubject);
            textViewMajor = itemView.findViewById(R.id.textViewMajor);

            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonRedo = itemView.findViewById(R.id.buttonRedo);
        }

    }
}


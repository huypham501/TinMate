package com.hcmus.tinuni.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.Objects;

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

    private boolean check = false;

    public void deleteCommit() {
        if (arrayListDemandId.isEmpty()) {
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Demands").child(userId);

        for (String demandId : arrayListDemandId) {
            databaseReference.child(demandId).removeValue().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    new AlertDialog.Builder(context).setTitle("Error").setMessage("Something went wrong...\nAction has been broken").setNegativeButton("Ok", null);
                    check = true;
                }
            });

            if (check) {
                return;
            }
        }
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


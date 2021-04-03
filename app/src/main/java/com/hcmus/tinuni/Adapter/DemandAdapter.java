package com.hcmus.tinuni.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class DemandAdapter extends RecyclerView.Adapter<DemandAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Demand> demands;
    private boolean isEditorVisible;


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
        } else {
            holder.buttonDelete.setVisibility(View.INVISIBLE);
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
        private Button buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewSubject = itemView.findViewById(R.id.textViewSubject);
            textViewMajor = itemView.findViewById(R.id.textViewMajor);

            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonDelete.setVisibility(View.INVISIBLE);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("button delete clicked");
                }
            });

        }
    }
}


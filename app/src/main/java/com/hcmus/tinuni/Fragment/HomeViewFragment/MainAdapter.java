package com.hcmus.tinuni.Fragment.HomeViewFragment;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.tinuni.Activity.Profile.GroupProfileActivity;
import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    ArrayList<MainModel> mainModels;
    Context context;

    public MainAdapter(ArrayList<MainModel> mainModels, Context context) {
        this.mainModels = mainModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.room_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    //holder.imageViewRoom.setImageResource(mainModels.get(position).getRoomAvatar());
    Glide.with(context).load(mainModels.get(position).getRoomAvatar()).into((holder.imageViewRoom));
    holder.textViewRoomName.setText(mainModels.get(position).getRoomName());
    holder.textViewRoomAmount.setText(mainModels.get(position).getRoomAmount()+" Thanh Vien");
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, GroupProfileActivity.class);
            i.putExtra("id",mainModels.get(position).getId());
            context.startActivity(i);
        }
    });
    }

    @Override
    public int getItemCount() {
        return mainModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewRoom;
        TextView textViewRoomName;
        TextView textViewRoomAmount;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageViewRoom = itemView.findViewById(R.id.imageViewRoom);
            textViewRoomName = itemView.findViewById(R.id.textViewRoomName);
            textViewRoomAmount = itemView.findViewById(R.id.textViewNumberOfMembers);
        }
    }
}

package com.hcmus.tinuni.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.ViewHolder> {
    //---------------------------------------------------------------------
    ArrayList<User> mList;
    Context context;
    String ban_unban;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root =  db.getReference().child("Users");
    //---------------------------------------------------------------------
    public ManageUserAdapter(Context context, ArrayList<User> mList){
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public ManageUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.admin_manage_user_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageUserAdapter.ViewHolder holder, int position) {
        User user = mList.get(position);

        if (user.getImageURL().equals("default")) {
            holder.avatar.setImageResource(R.drawable.profile_image);
        } else {
            Glide.with(context).load(user.getImageURL()).into(holder.avatar);
        }

        holder.email.setText(user.getEmail());
        holder.name.setText(user.getUserName());
        holder.gender.setText(user.getGender());

        ban_unban = user.getBanned();
        if (ban_unban.equals("True")){
            //Dang bi ban -> set nut la UNBAN
            holder.unban_btn.setVisibility(View.VISIBLE);
            holder.ban_btn.setVisibility(View.INVISIBLE);
        } else{
            //Khong bi ban -> set nut la BAN
            holder.ban_btn.setVisibility(View.VISIBLE);
            holder.unban_btn.setVisibility(View.INVISIBLE);
        }

        holder.ban_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.unban_btn.setVisibility(View.VISIBLE);
                holder.ban_btn.setVisibility(View.INVISIBLE);
                root.child(user.getId()).child("banned").setValue("True");
            }
        });
        holder.unban_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ban_btn.setVisibility(View.VISIBLE);
                holder.unban_btn.setVisibility(View.INVISIBLE);
                root.child(user.getId()).child("banned").setValue("False");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

   public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView avatar;
        Button ban_btn, unban_btn;
        TextView email, name, gender;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            avatar = itemView.findViewById(R.id.item_user_ava);
            ban_btn = itemView.findViewById(R.id.admin_ban_button);
            unban_btn = itemView.findViewById(R.id.admin_unban_button);
            email = itemView.findViewById(R.id.item_user_email);
            name = itemView.findViewById(R.id.item_user_fullname);
            gender = itemView.findViewById(R.id.item_user_gender);
        }
    }

}

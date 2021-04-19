package com.hcmus.tinuni.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.ViewHolder> {
    ArrayList<User> mList;
    Context context;

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


        //Neu attribute "banned" == 1 -> Tai khoan dang bi ban, == 0 -> Tai khoan binh thuong
        //Neu tk dang bi ban
//        holder.ban_btn.setText("UNBAN");
//        holder.ban_btn.setBackgroundResource(R.drawable.unban_button);

        //Nhan nut "BAN" -> ghi len firebase attribute "banned" = 1
        //Nhan nut "UNBAN" -> ghi len firebase attribute "banned" = 0
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

   public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView avatar;
        Button ban_btn;
        TextView email, name, gender;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            avatar = itemView.findViewById(R.id.item_user_ava);
            ban_btn = itemView.findViewById(R.id.admin_ban_button);
            email = itemView.findViewById(R.id.item_user_email);
            name = itemView.findViewById(R.id.item_user_fullname);
            gender = itemView.findViewById(R.id.item_user_gender);

        }
    }

}

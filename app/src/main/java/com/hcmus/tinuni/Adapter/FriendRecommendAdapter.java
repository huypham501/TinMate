package com.hcmus.tinuni.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hcmus.tinuni.Activity.Profile.ShowProfileActitivy;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.List;

public class FriendRecommendAdapter extends RecyclerView.Adapter<FriendRecommendAdapter.ViewHolder> {
    private Context context;
    private List<User> mItems;

    public FriendRecommendAdapter(Context context, List<User> mUsers) {
        this.context = context;
        this.mItems = mUsers;
    }

    @NonNull
    @Override
    public FriendRecommendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.friend_recommend_item,
                parent,
                false);
        return new FriendRecommendAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull FriendRecommendAdapter.ViewHolder holder, int position) {
        User user = mItems.get(position);

        holder.username.setText(user.getUserName());
        if (user.getImageURL().equals("default")) {
            holder.imageView.setImageResource(R.drawable.profile_image);
        } else {
            Glide.with(context)
                    .load(user.getImageURL())
                    .into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ShowProfileActitivy.class);
                i.putExtra("id", user.getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.groupName);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

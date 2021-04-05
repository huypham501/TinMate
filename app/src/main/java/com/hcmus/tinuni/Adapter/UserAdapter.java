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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.MessageActivity;
import com.hcmus.tinuni.Model.Chat;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> mUsers;
    private boolean isChat;

    String lastMessage;

    public UserAdapter(Context context, List<User> mUsers, boolean isChat) {
        this.context = context;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,
                parent,
                false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);

        holder.username.setText(user.getUserName());
        if (user.getImageURL().equals("default")) {
            holder.imageView.setImageResource(R.drawable.profile_image);
        } else {
            Glide.with(context)
                    .load(user.getImageURL())
                    .into(holder.imageView);
        }


        if(isChat)
            getLastMessage(user.getId(), holder.lastMessage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userId", user.getId());
                context.startActivity(i);
            }
        });
    }

    private void getLastMessage(String id, TextView txtLastMessage) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if ((chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(id)) ||
                            (chat.getSender().equals(id) && chat.getReceiver().equals(firebaseUser.getUid()))) {
                        lastMessage = chat.getMessage();
                    }
                }

                txtLastMessage.setText(lastMessage);
                lastMessage = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView imageView;
        private TextView lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            imageView = itemView.findViewById(R.id.imageView);
            lastMessage = itemView.findViewById(R.id.lastMessage);

        }
    }
}

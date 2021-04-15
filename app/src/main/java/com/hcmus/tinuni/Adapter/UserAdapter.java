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
import com.hcmus.tinuni.Model.ChatGroup;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> mItems;
    private boolean isChat;

    String lastMessage = "";
    String time = "";


    public UserAdapter(Context context, List<User> mUsers, boolean isChat) {
        this.context = context;
        this.mItems = mUsers;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_item,
                parent,
                false);
        return new UserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = mItems.get(position);

        holder.username.setText(user.getUserName());
        if (user.getImageURL().equals("default")) {
            holder.imageView.setImageResource(R.drawable.profile_image);
        } else {
            Glide.with(context)
                    .load(user.getImageURL())
                    .into(holder.imageView);
        }

        // Kiểm tra có chat hay không
        if (isChat) {
            getLastMessageFromUser(user.getId(), holder);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userId", user.getId());
                i.putExtra("groupId", "");
                context.startActivity(i);
            }
        });
//        } else {
//
//            Group group = (Group) item;
//
//            holder.username.setText(group.getName());
//
//            Glide.with(context)
//                    .load(group.getImageURL())
//                    .into(holder.imageView);
//
//            getLastMessageFromGroup(group.getId(), holder);
//
//
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(context, MessageActivity.class);
//                    i.putExtra("userId", "");
//                    i.putExtra("groupId", group.getId());
//                    context.startActivity(i);
//                }
//            });
//
//
//        }
    }

    private void getLastMessageFromUser(String id, ViewHolder holder) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);

                    if ((chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(id)) ||
                            (chat.getSender().equals(id) && chat.getReceiver().equals(firebaseUser.getUid()))) {
                        if (chat.getType().equals("text")) {
                            lastMessage = chat.getMessage();
                        } else if (chat.getType().equals("image")) {
                            lastMessage = "Image was sent";
                        }
                        time = chat.getTime();
                    }
                }

                if (!lastMessage.isEmpty())
                    holder.lastMessage.setText(lastMessage);
                if (!time.isEmpty())
                    holder.time.setText(holder.convertTime(time));

                lastMessage = "";
                time = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getLastMessageFromGroup(String groupId, ViewHolder holder) {
        final DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Groups")
                .child(groupId)
                .child("Messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatGroup chat = dataSnapshot.getValue(ChatGroup.class);

                    if (chat.getType().equals("text")) {
                        lastMessage = chat.getMessage();
                    } else if (chat.getType().equals("image")) {
                        lastMessage = "Image was sent";
                    }
                    time = chat.getTime();

                }

                if (!lastMessage.isEmpty())
                    holder.lastMessage.setText(lastMessage);
                if (!time.isEmpty())
                    holder.time.setText(holder.convertTime(time));

                lastMessage = "";
                time = "";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//        if (mItems.get(position) instanceof User) {
//            return ITEM_TYPE_USER;
//        } else {
//            return ITEM_TYPE_GROUP;
//        }
//    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private ImageView imageView;
        private TextView lastMessage;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            imageView = itemView.findViewById(R.id.imageView);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            time = itemView.findViewById(R.id.time);
        }

        public String convertTime(String time) {

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy h:mm a");
            String dateString = formatter.format(new Date(Long.parseLong(time)));

            // Nếu ngày hiện tại -> h:mm a
            // Nếu khác ngày hiện tại nhưng là ngày trong tuần thì hiện thứ mấy trong tuần
            // Nếu khác tháng thì hiện thêm tháng
            // Nếu khác năm thì hiện Ngày/Tháng/Năm

            return dateString;
        }
    }
}

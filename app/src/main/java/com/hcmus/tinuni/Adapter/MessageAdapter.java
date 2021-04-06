package com.hcmus.tinuni.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinuni.Model.Chat;
import com.hcmus.tinuni.Model.ChatGroup;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<Object> mItems;
    private List<String> imgURLs;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public MessageAdapter(Context context, List<Object> mItems, List<String> imgURLs) {
        this.context = context;
        this.mItems = mItems;
        this.imgURLs = imgURLs;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,
                    parent,
                    false);
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,
                    parent,
                    false);
            return new MessageAdapter.ViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Object item = mItems.get(position);
        if(item instanceof Chat) {
            Chat chat = (Chat) item;

            holder.showMessage.setText(chat.getMessage());

            if (imgURLs.get(0).equals("default")) {
                holder.profile_image.setImageResource(R.drawable.profile_image);
            } else {
                Glide.with(context)
                        .load(imgURLs.get(0))
                        .into(holder.profile_image);
            }
        } else {
            ChatGroup groupChat = (ChatGroup) item;

            holder.showMessage.setText(groupChat.getMessage());

            if (imgURLs.get(position).equals("default")) {
                holder.profile_image.setImageResource(R.drawable.profile_image);
            } else {
                Glide.with(context)
                        .load(imgURLs.get(position))
                        .into(holder.profile_image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    @Override
    public int getItemViewType(int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mItems.get(position) instanceof Chat) {
            Chat chat = (Chat) mItems.get(position);
            if (chat.getSender().equals(firebaseUser.getUid())) {
                return MSG_TYPE_RIGHT;
            } else {
                return MSG_TYPE_LEFT;
            }
        } else {
            ChatGroup chatGroup = (ChatGroup) mItems.get(position);
            if(chatGroup.getSender().equals(firebaseUser.getUid())) {
                return MSG_TYPE_RIGHT;
            } else {
                return MSG_TYPE_LEFT;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView showMessage;
        ImageView profile_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.showMessage);
            profile_image = itemView.findViewById(R.id.profile_image);


        }

    }
}

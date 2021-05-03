package com.hcmus.tinuni.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
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
import com.hcmus.tinuni.Activity.MainActivity;
import com.hcmus.tinuni.Activity.Profile.EditProfileActivity;
import com.hcmus.tinuni.Activity.Profile.ShowProfileActitivy;
import com.hcmus.tinuni.Activity.Profile.UserProfileActitivy;
import com.hcmus.tinuni.Activity.ShowMediaActivity;
import com.hcmus.tinuni.Activity.ShowZoomImage;
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

            if(chat.getType().equals("text")) {
                holder.showMessage.setVisibility(View.VISIBLE);
                holder.showImage.setVisibility(View.GONE);
                holder.showMedia.setVisibility(View.GONE);

                holder.showMessage.setText(chat.getMessage());
            } else if(chat.getType().equals("image")) {
                holder.showImage.setVisibility(View.VISIBLE);
                holder.showMessage.setVisibility(View.GONE);
                holder.showMedia.setVisibility(View.GONE);

                Glide.with(context.getApplicationContext())
                        .load(chat.getMessage())
                        .into(holder.showImage);

                holder.showImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent zoom_image = new Intent(context, ShowZoomImage.class);
                        zoom_image.putExtra("img_link", chat.getMessage());
                        context.startActivity(zoom_image);                }
                });

            } else if(chat.getType().equals("media")) {
                holder.showMedia.setVisibility(View.VISIBLE);
                holder.showImage.setVisibility(View.GONE);
                holder.showMessage.setVisibility(View.GONE);
                holder.showMedia.setText(URLUtil.guessFileName(chat.getMessage(), null, null));

                holder.showMedia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent show_media = new Intent(context, ShowMediaActivity.class);
                        show_media.putExtra("media_link", chat.getMessage());
                        context.startActivity(show_media);
                    }
                });
            }

            if (imgURLs.get(0).equals("default")) {
                holder.profile_image.setImageResource(R.drawable.profile_image);
            } else {
                Glide.with(context)
                        .load(imgURLs.get(0))
                        .into(holder.profile_image);
            }

            holder.profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_profile = new Intent(context, ShowProfileActitivy.class);
                    intent_profile.putExtra("userId", chat.getSender());
                    context.startActivity(intent_profile);
                }
            });

        } else {
            ChatGroup groupChat = (ChatGroup) item;

            if(groupChat.getType().equals("text")) {
                holder.showImage.setVisibility(View.GONE);
                holder.showMessage.setVisibility(View.VISIBLE);

                holder.showMessage.setText(groupChat.getMessage());
            } else if(groupChat.getType().equals("image")) {
                holder.showImage.setVisibility(View.VISIBLE);
                holder.showMessage.setVisibility(View.GONE);

                Glide.with(context.getApplicationContext())
                        .load(groupChat.getMessage())
                        .into(holder.showImage);

                holder.showImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent zoom_image = new Intent(context, ShowZoomImage.class);
                        zoom_image.putExtra("img_link", groupChat.getMessage());
                        context.startActivity(zoom_image);                }
                });
            } else if(groupChat.getType().equals("media")) {
                holder.showMedia.setVisibility(View.VISIBLE);
                holder.showImage.setVisibility(View.GONE);
                holder.showMessage.setVisibility(View.GONE);
                holder.showMedia.setText(URLUtil.guessFileName(groupChat.getMessage(), null, null));

                holder.showMedia.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent show_media = new Intent(context, ShowMediaActivity.class);
                        show_media.putExtra("media_link", groupChat.getMessage());
                        context.startActivity(show_media);
                    }
                });
            }

            if (imgURLs.get(position).equals("default")) {
                holder.profile_image.setImageResource(R.drawable.profile_image);
            } else {
                Glide.with(context)
                        .load(imgURLs.get(position))
                        .into(holder.profile_image);
            }

            holder.profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_profile = new Intent(context, ShowProfileActitivy.class);
                    intent_profile.putExtra("userId", groupChat.getSender());
                    context.startActivity(intent_profile);
                }
            });
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
        ImageView showImage;
        Button showMedia;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.showMessage);
            profile_image = itemView.findViewById(R.id.profile_image);
            showImage = itemView.findViewById(R.id.showImage);
            showMedia = itemView.findViewById(R.id.showMedia);
        }

    }
}

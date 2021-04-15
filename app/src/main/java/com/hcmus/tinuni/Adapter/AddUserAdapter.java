package com.hcmus.tinuni.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddUserAdapter extends RecyclerView.Adapter<AddUserAdapter.ViewHolder> {
    private Context context;
    private List<User> mItems;
    private String groupId;

    public AddUserAdapter(Context context, List<User> mUsers, String groupId) {
        this.context = context;
        this.mItems = mUsers;
        this.groupId = groupId;
    }

    @NonNull
    @Override
    public AddUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_item,
                parent,
                false);
        return new AddUserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AddUserAdapter.ViewHolder holder, int position) {
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
                //Add to group
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Groups");
                Map<String, String> map = new HashMap<>();
                map.put("id", user.getId());
                map.put("role", "member");
                mRef.child(groupId).child("Participants").child(user.getId()).setValue(map);
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
        private TextView lastMessage;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.groupName);
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

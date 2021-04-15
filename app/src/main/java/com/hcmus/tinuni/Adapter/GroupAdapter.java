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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private Context context;
    private List<Group> mItems;
    private boolean isChat;

    String lastMessage = "";
    String time = "";


    public GroupAdapter(Context context, List<Group> mGroups, boolean isChat) {
        this.context = context;
        this.mItems = mGroups;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.group_item,
                parent,
                false);
        return new GroupAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
        Group group = mItems.get(position);


        holder.groupName.setText(group.getName());

        Glide.with(context)
                .load(group.getImageURL())
                .into(holder.imageView);

        getLastMessageFromGroup(group.getId(), holder);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userId", "");
                i.putExtra("groupId", group.getId());
                context.startActivity(i);
            }
        });


    }


    private void getLastMessageFromGroup(String groupId, GroupAdapter.ViewHolder holder) {
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
        private TextView groupName;
        private ImageView imageView;
        private TextView lastMessage;
        private TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupName = itemView.findViewById(R.id.groupName);
            imageView = itemView.findViewById(R.id.imageView);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            time = itemView.findViewById(R.id.time);
        }

        // => Cần hour gap + last mess là thứ mấy + hôm nay là thứ mấy.
        public String convertTime(String time) {

            long lastMillis = Long.parseLong(time);
            long currentMillis = Long.parseLong(String.valueOf(System.currentTimeMillis()));
            long gapMillis = currentMillis - lastMillis;

            int gapHour = (int) ((gapMillis / (1000 * 60 * 60)));

            Calendar calendar = Calendar.getInstance();
            int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); //CN -> T7 = 1 -> 7
            int currentYear = calendar.get(Calendar.YEAR);

            calendar.setTime(new Date(lastMillis));
            int lastTimeDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int lastTimeDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            int lastTimeMonth = calendar.get(Calendar.MONTH) + 1;
            int lastTimeYear = calendar.get(Calendar.YEAR);
            int lastTimeHour = calendar.get(Calendar.HOUR);
            if (calendar.get(Calendar.AM_PM) == 1) //Sau 12h trua
                lastTimeHour += 12;
            int lastTimeMin = calendar.get(Calendar.MINUTE);
            //---------------------------------------------------------------------------------
            String result = "";

            if (gapHour < 24) {
                String strHour = String.valueOf(lastTimeHour);
                String strMin = String.valueOf(lastTimeMin);
                if (lastTimeHour < 10)
                    strHour = "0" + strHour;
                if (lastTimeMin < 10)
                    strMin = "0" + strMin;

                result = strHour + ":" + strMin;
            } else if (gapHour < 168 && lastTimeDayOfWeek < currentDayOfWeek) { //1 tuan co 168 tieng
                result = "Th" + String.valueOf(lastTimeDayOfWeek);
            } else {
                result = String.valueOf(lastTimeDayOfMonth) + " th " + String.valueOf(lastTimeMonth);
                if (lastTimeYear < currentYear) {
                    result += ", " + String.valueOf(lastTimeYear);
                }
            }

            return result;
        }
    }
}


package com.hcmus.tinuni.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.AddGroupActivity;
import com.hcmus.tinuni.Activity.Admin.AdminInitialActivity;
import com.hcmus.tinuni.Activity.Admin.AdminSettingGroupActivity;
import com.hcmus.tinuni.Activity.Authentication.ChangePasswordActivity;
import com.hcmus.tinuni.Activity.Authentication.SignInActivity;
import com.hcmus.tinuni.Activity.Profile.UserProfileActivity;
import com.hcmus.tinuni.Activity.SettingGroupActivity;
import com.hcmus.tinuni.Model.AdminAction;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ManageGroupChatAdapter extends RecyclerView.Adapter<ManageGroupChatAdapter.ViewHolder> implements Filterable {
    //---------------------------------------------------------------------
    ArrayList<Group> mList;
    ArrayList<Group> mListAll;
    Context context;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root =  db.getReference().child("Groups");

    //---------------------------------------------------------------------
    public ManageGroupChatAdapter(Context context, ArrayList<Group> mList, ArrayList<Group> mListAll){
        this.mList = mList;
        this.mListAll = mListAll;
        this.context = context;
    }

    @NonNull
    @Override
    public ManageGroupChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.admin_group_chat_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageGroupChatAdapter.ViewHolder holder, int position) {
        Group group = mList.get(position);

        holder.admin_group_chat_name.setText(group.getName());

        Glide.with(context)
                .load(group.getImageURL())
                .into(holder.admin_group_chat_ava);

        root.child(group.getId()).child("Participants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.group_member.setText(String.valueOf(snapshot.getChildrenCount()) + " members");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.delete_group_chat_btn.setVisibility(View.INVISIBLE);
        holder.delete_group_chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Delete this group chat")
                        .setMessage("This action cannot be undone, do it anyway?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               //Remove all ChatList data of users that relate to this group chat.
//                                db.getReference().child("ChatList").addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
////                                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
////                                            for (DataSnapshot dataSnapshot2: dataSnapshot){
////
////                                            }
////
////                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//                                //Remove group chat.
//                                root.child(group.getId()).removeValue();
//
//                                Toast.makeText(v.getContext(), "Group chat deleted", Toast.LENGTH_SHORT).show();
//
//                                String currentMillis = String.valueOf(System.currentTimeMillis());
//                                AdminAction adminAction = new AdminAction(currentMillis,
//                                        "Delete group chat", "Delete " + group.toString());
//                                db.getReference().child("AdminActions").push().setValue(adminAction);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminSettingGroupActivity.class);
                intent.putExtra("groupId", group.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    //Filter searchh result, this one will be used by Search View
    public Filter getFilter() {
        return filter;
    }

    //Make a filter
    Filter filter = new Filter() {
        //Run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Group> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(mListAll);
            } else {
                for (Group group : mListAll) {
                    if (group.toString().toLowerCase().contains(constraint.toString().toLowerCase().trim())) {
                        filteredList.add(group);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;

            return filterResults;
        }

        //Run on UI thread
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mList.clear();
            mList.addAll((ArrayList<Group>) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView admin_group_chat_ava;
        private TextView admin_group_chat_name;
        private TextView group_member;
        ImageButton delete_group_chat_btn;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            admin_group_chat_ava = itemView.findViewById(R.id.admin_group_chat_ava);
            admin_group_chat_name = itemView.findViewById(R.id.admin_group_chat_name);
            group_member = itemView.findViewById(R.id.group_member);
            delete_group_chat_btn = itemView.findViewById(R.id.delete_group_chat_btn);
        }
    }

}

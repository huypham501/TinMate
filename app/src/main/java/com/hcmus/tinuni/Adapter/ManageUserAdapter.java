package com.hcmus.tinuni.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinuni.Model.AdminAction;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.ViewHolder> implements Filterable {
    //---------------------------------------------------------------------
    ArrayList<User> mList;
    ArrayList<User> mListAll;
    Context context;
    String ban_unban;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root =  db.getReference().child("Users");
    //---------------------------------------------------------------------
    public ManageUserAdapter(Context context, ArrayList<User> mList){
        this.mList = mList;
        this.mListAll = new ArrayList<>(mList);
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

                String currentMillis = String.valueOf(System.currentTimeMillis());
                AdminAction adminAction = new AdminAction(currentMillis, "Ban user", "Ban " + user.getEmail());
                db.getReference().child("AdminActions").push().setValue(adminAction);
            }
        });
        holder.unban_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.ban_btn.setVisibility(View.VISIBLE);
                holder.unban_btn.setVisibility(View.INVISIBLE);
                root.child(user.getId()).child("banned").setValue("False");

                String currentMillis = String.valueOf(System.currentTimeMillis());
                AdminAction adminAction = new AdminAction(currentMillis, "Unban user", "Unban " + user.getEmail());
                db.getReference().child("AdminActions").push().setValue(adminAction);
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

            ArrayList<User> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(mListAll);
            } else {
                for (User user: mListAll) {
                    if (user.getEmail().toLowerCase().contains(constraint.toString().toLowerCase().trim())){
                        filteredList.add(user);
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
            mList.addAll((ArrayList<User>) results.values);
            //mList = mListAll;
            notifyDataSetChanged();
        }
    };

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

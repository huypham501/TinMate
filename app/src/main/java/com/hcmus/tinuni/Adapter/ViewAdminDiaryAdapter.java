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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hcmus.tinuni.Activity.Admin.AdminInitialActivity;
import com.hcmus.tinuni.Activity.Authentication.SignInActivity;
import com.hcmus.tinuni.Model.AdminAction;
import com.hcmus.tinuni.Model.ReportMessage;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ViewAdminDiaryAdapter extends RecyclerView.Adapter<ViewAdminDiaryAdapter.ViewHolder> {
    //---------------------------------------------------------------------
    ArrayList<AdminAction> mList;
    Context context;
    String ban_unban;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root =  db.getReference().child("AdminActions");
    //---------------------------------------------------------------------
    public ViewAdminDiaryAdapter(Context context, ArrayList<AdminAction> mList){
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewAdminDiaryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.admin_diary_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdminDiaryAdapter.ViewHolder holder, int position) {
        AdminAction adminAction = mList.get(position);

        Calendar calendar = Calendar.getInstance();
        long reportTime = Long.parseLong(adminAction.getTime());
        calendar.setTime(new Date(reportTime));

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        String dd = String.valueOf(day);
        String mm = String.valueOf(month);
        String yyyy = String.valueOf(year);

        if (day < 10)
            dd = "0" + dd;
        if (month < 10)
            mm = "0" + mm;

        String time = dd + "-" + mm + "-" + yyyy;

        holder.admin_action.setText(adminAction.getAction());
        holder.action_time.setText(time);
        holder.diary_detail.setText(adminAction.getDetail());


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView admin_action, action_time, diary_detail;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            admin_action = itemView.findViewById(R.id.admin_action);
            action_time = itemView.findViewById(R.id.action_time);
            diary_detail = itemView.findViewById(R.id.diary_detail);
        }
    }

}

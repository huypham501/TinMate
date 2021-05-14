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

public class ViewAdminDiaryAdapter extends RecyclerView.Adapter<ViewAdminDiaryAdapter.ViewHolder> implements Filterable {
    //---------------------------------------------------------------------
    ArrayList<AdminAction> mList;
    ArrayList<AdminAction> mListAll;
    Context context;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("AdminActions");

    //---------------------------------------------------------------------
    public ViewAdminDiaryAdapter(Context context, ArrayList<AdminAction> mList, ArrayList<AdminAction> mListAll) {
        this.mList = mList;
        this.mListAll = mListAll;
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
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String dd = String.valueOf(day);
        String mm = String.valueOf(month);
        String yyyy = String.valueOf(year);
        String hh = String.valueOf(hour);
        String minmin = String.valueOf(minute);
        String ss = String.valueOf(second);

        if (day < 10)
            dd = "0" + dd;
        if (month < 10)
            mm = "0" + mm;
        if (hour < 10)
            hh = "0" + hh;
        if (minute < 10)
            minmin = "0" + minmin;
        if (second < 10)
            ss = "0" + ss;

        String time = dd + "-" + mm + "-" + yyyy;
        String detailTime = hh + ":" + minmin + ":" + ss;

        holder.admin_action.setText(adminAction.getAction());
        holder.action_time.setText(time);
        holder.diary_detail.setText(adminAction.getDetail());
        holder.detail_time.setText(detailTime);

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

            ArrayList<AdminAction> filteredList = new ArrayList<>();

            if (constraint.toString().isEmpty()) {
                filteredList.addAll(mListAll);
            } else {
                for (AdminAction adminAction : mListAll) {
                    if (adminAction.toString().toLowerCase().contains(constraint.toString().toLowerCase().trim())) {
                        filteredList.add(adminAction);
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
            mList.addAll((ArrayList<AdminAction>) results.values);
            notifyDataSetChanged();
        }
    };


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView admin_action, action_time, diary_detail, detail_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            admin_action = itemView.findViewById(R.id.admin_action);
            action_time = itemView.findViewById(R.id.action_time);
            diary_detail = itemView.findViewById(R.id.diary_detail);
            detail_time = itemView.findViewById(R.id.detail_time);
        }
    }

}

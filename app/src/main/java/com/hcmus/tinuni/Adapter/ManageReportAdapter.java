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

public class ManageReportAdapter extends RecyclerView.Adapter<ManageReportAdapter.ViewHolder> {
    //---------------------------------------------------------------------
    ArrayList<ReportMessage> mList;
    Context context;
    String ban_unban;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root =  db.getReference().child("ReportMessages");
    //---------------------------------------------------------------------
    public ManageReportAdapter(Context context, ArrayList<ReportMessage> mList){
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public ManageReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.admin_manage_report_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageReportAdapter.ViewHolder holder, int position) {
        ReportMessage reportMessage = mList.get(position);

        Calendar calendar = Calendar.getInstance();
        long reportTime = Long.parseLong(reportMessage.getTime());
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

        holder.crime_tag.setText(reportMessage.getCrimeTag());
        holder.report_time.setText(time);
        holder.owner_email.setText(reportMessage.getOwnerEmail());
        holder.target_email.setText(reportMessage.getTargetEmail());

        holder.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(R.drawable.info)
                        .setTitle("Desciption")
                        .setMessage(reportMessage.getDescription())
                        .show();
            }
        });

        holder.delete_report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Delete this report")
                        .setMessage("This action cannot be undone, do it anyway?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                root.child(reportMessage.getId()).removeValue();
                                Toast.makeText(v.getContext(), "Report deleted", Toast.LENGTH_SHORT).show();

                                String currentMillis = String.valueOf(System.currentTimeMillis());
                                AdminAction adminAction = new AdminAction(currentMillis, "Delete report", "Delete " + reportMessage.toString());
                                db.getReference().child("AdminActions").push().setValue(adminAction);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView crime_tag, owner_email, target_email, report_time;
        ImageButton delete_report_btn;
        ImageView btn_more;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            crime_tag = itemView.findViewById(R.id.crime_tag);
            report_time = itemView.findViewById(R.id.report_time);
            owner_email = itemView.findViewById(R.id.owner_email);
            target_email = itemView.findViewById(R.id.target_email);

            delete_report_btn = itemView.findViewById(R.id.delete_report_btn);
            btn_more = itemView.findViewById(R.id.btn_more);
        }
    }

}

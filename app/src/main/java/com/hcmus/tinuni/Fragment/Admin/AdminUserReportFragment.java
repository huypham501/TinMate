package com.hcmus.tinuni.Fragment.Admin;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;
import com.hcmus.tinuni.Adapter.ManageReportAdapter;
import com.hcmus.tinuni.Adapter.ManageUserAdapter;
import com.hcmus.tinuni.Model.ReportMessage;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class AdminUserReportFragment extends Fragment {
    //-----------------------------------------------------
    private RecyclerView recyclerView;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root =  db.getReference().child("ReportMessages");
    private  ValueEventListener valueEventListener;
    private ManageReportAdapter adapter;
    private ArrayList<ReportMessage> list;
    private ArrayList<ReportMessage> listAll;
    private SearchView searchView;

    String searchingText = "";

    //-----------------------------------------------------
    public AdminUserReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_user_report, container, false);

        //Animated background
        RelativeLayout relativeLayout = view.findViewById(R.id.admin_user_animated_gradient_bg);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        //Set up Search View
        searchView = (SearchView) view.findViewById(R.id.admin_search_for_report);

        //Set up recycler view
        recyclerView = view.findViewById(R.id.recyclerView_manage_report_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        listAll = new ArrayList<>();
        adapter = new ManageReportAdapter(getContext(), list, listAll);
        recyclerView.setAdapter(adapter);

        //Load data from Firebase
        root.addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                listAll.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ReportMessage reportMessage = dataSnapshot.getValue(ReportMessage.class);
                    reportMessage.setId(dataSnapshot.getKey());

                    list.add(reportMessage);
                    listAll.add(reportMessage);
                }

                adapter.getFilter().filter(searchingText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //------------------------------Search View------------------------------------
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchingText = newText;
                adapter.getFilter().filter(newText);

                return false;
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        root.removeEventListener(valueEventListener);
    }
}
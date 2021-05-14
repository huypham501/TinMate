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
import com.hcmus.tinuni.Adapter.ManageReportAdapter;
import com.hcmus.tinuni.Adapter.ManageUserAdapter;
import com.hcmus.tinuni.Adapter.ViewAdminDiaryAdapter;
import com.hcmus.tinuni.Model.AdminAction;
import com.hcmus.tinuni.Model.ReportMessage;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.ArrayList;

public class AdminDiaryFragment extends Fragment {
    //-----------------------------------------------------
    private RecyclerView recyclerView;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root =  db.getReference().child("AdminActions");
    private ViewAdminDiaryAdapter adapter;
    private ArrayList<AdminAction> list;
    private ArrayList<AdminAction> listAll;
    private SearchView searchView;

    String searchingText = "";

    //-----------------------------------------------------
    public AdminDiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_diary, container, false);

        //Animated background
        RelativeLayout relativeLayout = view.findViewById(R.id.admin_user_animated_gradient_bg);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        //Set up Search View
        searchView = (SearchView) view.findViewById(R.id.admin_search_for_diary);

        //Set up recycler view
        recyclerView = view.findViewById(R.id.recyclerView_view_diary);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        listAll = new ArrayList<>();
        adapter = new ViewAdminDiaryAdapter(getContext(), list, listAll);
        recyclerView.setAdapter(adapter);

        //Load data from Firebase
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                listAll.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    AdminAction adminAction = dataSnapshot.getValue(AdminAction.class);

                    list.add(adminAction);
                    listAll.add(adminAction);
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


}
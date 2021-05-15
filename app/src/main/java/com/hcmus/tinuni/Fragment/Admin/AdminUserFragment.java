package com.hcmus.tinuni.Fragment.Admin;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Adapter.ManageUserAdapter;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdminUserFragment extends Fragment {
    //-----------------------------------------------------
    private RecyclerView recyclerView;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users");
    private ValueEventListener valueEventListener;
    private ManageUserAdapter adapter;
    private ArrayList<User> list;
    private ArrayList<User> listAll;
    private SearchView searchView;

    String searchingText = "";

    //-----------------------------------------------------
    public AdminUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_user, container, false);

        //Animated background
        RelativeLayout relativeLayout = view.findViewById(R.id.admin_user_animated_gradient_bg);
        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        //Set up Search View
        searchView = (SearchView) view.findViewById(R.id.admin_search_for_user);

        //Set up recycler view
        recyclerView = view.findViewById(R.id.recyclerView_manage_user_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        listAll = new ArrayList<>();
        adapter = new ManageUserAdapter(getContext(), list, listAll);
        recyclerView.setAdapter(adapter);

        //Load data from Firebase
        root.addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                listAll.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);

                    list.add(user);
                    listAll.add(user);
                    //An said if there are 1000 users, admin must wait for too long until the whole list is fully loaded.
                    //So, should leave the adapter.notifyDataSetChanged() here
                }

                //The list is always being filtered, in normal state, it's filter by NULL - which means by nothing
                //-> Then the adapter will load full list.
                //In order cases, just get the searching text then filter by it.
                adapter.getFilter().filter(searchingText);
                //the called method above contains notifyDatasetChange() in publishResults() of Filter
                //So, we don't need to call it in the "for loop" above, if we do, when click BAN/UNBAN, list flickers.
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

    //There is 1 small lack of logic, whose solution require Activity - Fragment communication.
    //I won't do that, just gonna leave the link here.
    //https://youtu.be/9xpvAjirN2s?t=1857.

    //And, i think when user click Submit button on keyboard when the SearchView is empty
    //It should trigger onQueryTextSubmit, but doing that require customizing SearchView
    //And I dont have time, here is the way https://stackoverflow.com/questions/13576283/android-searchview-onquerytextlistener-onquerytextsubmit-not-fired-on-empty-quer
}
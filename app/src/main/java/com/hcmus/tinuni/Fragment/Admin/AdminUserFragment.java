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
    private ManageUserAdapter adapter;
    private ArrayList<User> list;
    private SearchView searchView;

    boolean searching = false;

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
        searchView = (SearchView) view.findViewById(R.id.admin_search_user);

        //Set up recycler view
        recyclerView = view.findViewById(R.id.recyclerView_manage_user_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        adapter = new ManageUserAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

//        I dont know why but if you leave this block of code here, it cause 2 bugs
//          1. The user list is duplicated after each data update to firebase.
//            2. Flashy button: When you click on BAN/UNBAN button, it flash
//                (idk how to describe it, but just enable this block and see it yourself)

        //Load data from Firebase
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!searching) {
                    list.clear();
//              list = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        list.add(user);
                    }

                    adapter.notifyDataSetChanged(); //-> old way
                    //I guess the new way below mean each time when data change, you create a whole new view\
                    //The new way is ok but still 1 problem, when data change, it scroll back to the top of list
//                adapter = new ManageUserAdapter(getContext(), list);
//                recyclerView.setAdapter(adapter);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //------------------------------Search View------------------------------------
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searching = false;
                searchView.clearFocus();
                return true;
            }

            @Override
            //Search directly on Firebase realtime database.
            public boolean onQueryTextChange(String newText) {
                System.out.println("*******************************");
                searching = true;

                root.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            list.add(user);
                        }

                        adapter = new ManageUserAdapter(getContext(), list);
                        adapter.getFilter().filter(newText);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                return false;
            }
        });

        return view;
    }

    //There is 1 small lack of logic, whose solution require Activity - Fragment communication.
    //I won't do that, just gonna leave the link here.
    //https://youtu.be/9xpvAjirN2s?t=1857.

    //And, i think when user click Submit button on keyboard when the SearchView is empty
    //It should trigger onQueryTextSubmit, but doing that require customizing SearchView
    //And I dont have time, here is the way https://stackoverflow.com/questions/13576283/android-searchview-onquerytextlistener-onquerytextsubmit-not-fired-on-empty-quer

    //Finally, when searchView lost focus, only search results are left
    //But when new data is fetched, the results will renew as a whole new list of all users.
}
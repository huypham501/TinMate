package com.hcmus.tinuni.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.MessageActivity;
import com.hcmus.tinuni.Model.Demand;
import com.hcmus.tinuni.Model.Group;
import com.hcmus.tinuni.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class MatchingAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<HashMap<String, Object>> arrayListGroup = new ArrayList<>();

    public MatchingAdapter(Context context, ArrayList<HashMap<String, Object>> arrayListGroup) {
        this.context = context;
        this.arrayListGroup = arrayListGroup;
    }

    public MatchingAdapter(Context context) {
        this.context = context;
    }

    public MatchingAdapter(MatchingFragment matchingFragment) {
    }

    @Override
    public int getCount() {
        return arrayListGroup.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        HashMap<String, Object> hashMapGroup = arrayListGroup.get(position);

        View view = layoutInflater.inflate(R.layout.room_matching, container, false);
        ImageView imageView = view.findViewById(R.id.imageRoom);

        TextView textViewGroupName = view.findViewById(R.id.textViewGroupName);
        TextView textViewSchool = view.findViewById(R.id.textViewSchool);
        TextView textViewSubject = view.findViewById(R.id.textViewSubject);
        TextView textViewMajor = view.findViewById(R.id.textViewMajor);
        TextView textViewNumberMembers = view.findViewById(R.id.textViewNumberMembers);

        Button buttonJoin = view.findViewById(R.id.buttonJoin);
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupId = hashMapGroup.get("id").toString();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference databaseReferenceGroups = FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Participants");
                databaseReferenceGroups.child(userId).child("id").setValue(userId).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {

                        } else {
                            databaseReferenceGroups.child(userId).child("role").setValue("member").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()) {

                                    } else {
                                        DatabaseReference databaseReferenceChatList = FirebaseDatabase.getInstance().getReference("ChatList").child(userId);
                                        databaseReferenceChatList.child(groupId).child("id").setValue(groupId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //MOVE TO GROUP
                                                Intent intent = new Intent(context, MessageActivity.class);
                                                intent.putExtra("userId", "");
                                                intent.putExtra("groupId", groupId);
                                                context.startActivity(intent);
                                            }
                                        });
                                    }
                                }
                            });
                        }

                    }
                });
            }
        });

        if (hashMapGroup.get("imageURL").equals("default")) {
            imageView.setImageResource(R.drawable.room_1);
        } else {
            Glide.with(context)
                    .load(hashMapGroup.get("imageURL"))
                    .into(imageView);
        }

        textViewGroupName.setText(hashMapGroup.get("name").toString());
        textViewSubject.setText(hashMapGroup.get("subject").toString());
        textViewSchool.setText(hashMapGroup.get("school").toString());
        textViewMajor.setText(hashMapGroup.get("major").toString());
        textViewNumberMembers.setText(hashMapGroup.get("numberMembers").toString() + " members");

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

public class MatchingFragment extends Fragment {

    private ViewPager viewPager;
    private MatchingAdapter matchingAdapter;
    private RelativeLayout relativeLayoutWarningEmptyMatch;

    private String userId;

    Query queryLoadSuggestGroup;
    ValueEventListener valueEventListenerLoadSuggestGroup;

    public MatchingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_matching, container, false);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        viewPager = view.findViewById(R.id.viewPagerMatching);

        relativeLayoutWarningEmptyMatch = view.findViewById(R.id.relativeLayoutWarningEmptyMatch);
        relativeLayoutWarningEmptyMatch.setVisibility(View.GONE);

        setupLoadSuggestGroup();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        queryLoadSuggestGroup.addValueEventListener(valueEventListenerLoadSuggestGroup);
    }

    @Override
    public void onStop() {
        super.onStop();
        queryLoadSuggestGroup.removeEventListener(valueEventListenerLoadSuggestGroup);
    }

    private void setupLoadSuggestGroup() {
        ArrayList<String> arrayListGroupId = new ArrayList<>();

        queryLoadSuggestGroup = FirebaseDatabase.getInstance().getReference("Demands").orderByChild("userId").equalTo(userId);
        valueEventListenerLoadSuggestGroup = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotDemands) {
                if (!snapshotDemands.exists()) {
                    // VISIBLE WARNING EMPTY
                    viewPager.setAdapter(null);
                    relativeLayoutWarningEmptyMatch.setVisibility(View.VISIBLE);
                } else {
                    // GONE WARNING EMPTY
                    relativeLayoutWarningEmptyMatch.setVisibility(View.GONE);

                    for (DataSnapshot dataSnapshot : snapshotDemands.getChildren()) {
                        Demand demand = dataSnapshot.getValue(Demand.class);

                        Query queryGroupId = FirebaseDatabase.getInstance().getReference("Groups").orderByChild("subject").equalTo(demand.getSubject());
                        queryGroupId.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshotGroups) {
                                for (DataSnapshot dataSnapshot1 : snapshotGroups.getChildren()) {
                                    if (dataSnapshot1.child("Participants").child(userId).getValue() == null) {
                                        arrayListGroupId.add(dataSnapshot1.getKey());
                                    }
                                }

                                if (arrayListGroupId.size() != 0) {
                                    // REMOVE DUPLICATE
                                    Set<String> set = new HashSet<>(arrayListGroupId);
                                    arrayListGroupId.clear();
                                    arrayListGroupId.addAll(set);
                                    // END REMOVE DUPLICATE

                                    //Shuffle array list group
                                    Collections.shuffle(arrayListGroupId);

                                    ArrayList<HashMap<String, Object>> arrayListGroup = new ArrayList<>();

                                    matchingAdapter = new MatchingAdapter(getContext(), arrayListGroup);
                                    viewPager.setAdapter(matchingAdapter);

                                    for (String strGroupId : arrayListGroupId) {
                                        DatabaseReference databaseReferenceGroups = FirebaseDatabase.getInstance().getReference("Groups").child(strGroupId);

                                        databaseReferenceGroups.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot groupSnapshot) {
                                                if (!groupSnapshot.exists()) {

                                                } else {
                                                    Group group = groupSnapshot.getValue(Group.class);
                                                    HashMap<String, Object> hashMapGroup = Group.toHashMap(group);

                                                    long numberMembers = groupSnapshot.child("Participants").getChildrenCount();
                                                    hashMapGroup.put("numberMembers", numberMembers);

                                                    arrayListGroup.add(hashMapGroup);
                                                    matchingAdapter = new MatchingAdapter(getContext(), arrayListGroup);
                                                    viewPager.setAdapter(matchingAdapter);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                } else {
                                    // VISIBLE WARNING EMPTY
                                    viewPager.setAdapter(null);
                                    relativeLayoutWarningEmptyMatch.setVisibility(View.VISIBLE);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }
}
package com.hcmus.tinuni.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hcmus.tinuni.Activity.MainActivity;
import com.hcmus.tinuni.Fragment.HomeViewFragment.MajorRoomSlider;
import com.hcmus.tinuni.Model.User;
import com.hcmus.tinuni.R;

import java.util.Timer;
import java.util.TimerTask;

class ImageAdapter extends PagerAdapter {

    private Context mContext;
    public int[] mImageIds = { R.drawable.work_alone, R.drawable.brainstorming} ;

    public String[] shortTextSliders = {"Learning by yourself?","Learning in group"};

    public String[] longTextSliders = {"Try learning together, more effectively.",
            "Helping each other, solve problem faster, more talking"};

    public ImageAdapter(Context context){
        mContext = context;
    }

    public ImageAdapter(HomeFragment homeFragment) {
    }

    @Override
    public int getCount() {
        return mImageIds.length ;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout_home_page,container,false);

        ImageView slideImageView = view.findViewById(R.id.iconSlider);
        TextView slideShortText = view.findViewById(R.id.shotTextSlider);
        TextView slideLongText = view.findViewById(R.id.longTextSlider);

        slideImageView.setImageResource(mImageIds[position]);
        slideShortText.setText(shortTextSliders[position]);
        slideLongText.setText(longTextSliders[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}


public class HomeFragment extends Fragment {

    Timer timer;

    public HomeFragment() {
        // Required empty public constructor
    }

    public ViewPager viewPagerHome;
    private LinearLayout mDotLayout;
    ImageAdapter adapter;

    private TextView[] mDots;

    private Button mButtonBack;
    private Button mButtonNext;

    private int mCurrentPage;

    private FirebaseUser mUser;
    private DatabaseReference mRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);

//        Toolbar toolbar = view.findViewById(R.id.toolbar);
//        TextView tabName = (TextView) toolbar.getChildAt(1);
//        tabName.setText("Home");

        mUser = FirebaseAuth.getInstance()
                .getCurrentUser();
        mRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(mUser.getUid());

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

//                ImageView imageView = (ImageView) toolbar.getChildAt(0);
//                if (user.getImageURL().equals("default")) {
//                    imageView.setImageResource(R.drawable.profile_image);
//                } else {
//                    Glide.with(HomeFragment.this)
//                            .load(user.getImageURL())
//                            .into(imageView);
//
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewPagerHome = view.findViewById(R.id.viewPagerHome);
        mDotLayout = view.findViewById(R.id.dots);

        mButtonBack= view.findViewById(R.id.buttonBackSlide);
        mButtonNext= view.findViewById(R.id.buttonNextSlide);

        adapter = new ImageAdapter(getContext());

        viewPagerHome.setAdapter(adapter);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                viewPagerHome.post(new Runnable() {
                    @Override
                    public void run() {
                        viewPagerHome.setCurrentItem((viewPagerHome.getCurrentItem()+1)%adapter.mImageIds.length);
                    }
                });
            }
        };

        timer = new Timer();
        timer.schedule(timerTask,6000,6000);
        addDotsIndicator(0);

        viewPagerHome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addDotsIndicator(position);
                mCurrentPage = position;

                if(position==0){
                    mButtonNext.setEnabled(true);
                    mButtonBack.setEnabled(false);
                    mButtonBack.setVisibility(View.INVISIBLE);
                    mButtonNext.setVisibility(View.VISIBLE);
                }
                else if(position == mDots.length - 1){
                    mButtonNext.setEnabled(false);
                    mButtonBack.setEnabled(true);
                    mButtonNext.setVisibility(View.INVISIBLE);
                    mButtonBack.setVisibility(View.VISIBLE);
                }
                else{
                    mButtonNext.setEnabled(true);
                    mButtonBack.setEnabled(true);
                    mButtonNext.setVisibility(View.VISIBLE);
                    mButtonBack.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPagerHome.setCurrentItem(mCurrentPage+1);
            }
        });

        mButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPagerHome.setCurrentItem(mCurrentPage-1);
            }
        });

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft_add = fm.beginTransaction();
        ft_add.add(R.id.frameLayoutMajorRooms,new MajorRoomSlider());
        ft_add.commit();

        return view;
    }

    public void addDotsIndicator(int position){
        mDots = new TextView[adapter.mImageIds.length];
        mDotLayout.removeAllViews();
        for(int i=0;i<mDots.length;i++){
            mDots[i] = new TextView(getContext());
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(50);
            mDots[i].setTextColor(getResources().getColor(R.color.lighterBlack));

            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }


}
package com.hcmus.tinuni.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcmus.tinuni.R;

class ImageAdapter extends PagerAdapter {

    private Context mContext;
    public int[] mImageIds = { R.drawable.work_alone} ;

    public String[] shortTextSliders = {"Learning by yourself?"};

    public String[] longTextSliders = {"Why don't you learning in group? Try learning together, more effectively."};

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

    public HomeFragment() {
        // Required empty public constructor
    }

    private ViewPager viewPagerHome;
    private LinearLayout mDotLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPagerHome = view.findViewById(R.id.viewPagerHome);
        mDotLayout = view.findViewById(R.id.dots);

        ImageAdapter adapter = new ImageAdapter(getContext());
        viewPagerHome.setAdapter(adapter);
        return view;
    }
}
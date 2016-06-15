package com.gill.travelmate.fragments;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gill.travelmate.HomeActivity;
import com.gill.travelmate.R;
import com.gill.travelmate.utils.FontHelper;
import com.gill.travelmate.utils.GeneralValues;
import com.gill.travelmate.utils.TinyDB;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */

//This is main fragment in which all three fragment infulates
public class MainFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TinyDB tinyDB;
    private Context mContext;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_main, container, false);

        FontHelper.applyFont(getActivity(), v.findViewById(R.id.container_mainfragment), "bauhaus.ttf");

        mContext=getActivity();
        tinyDB=new TinyDB(mContext);

        //set fragments into the view pager
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        assert viewPager != null;
        tabLayout.setupWithViewPager(viewPager);
        changeTabsFont();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    HomeActivity.filterIcon.setVisibility(View.GONE);
                }else{
                    HomeActivity.filterIcon.setVisibility(View.VISIBLE);
                }

                if(position==0){
                    tinyDB.putBoolean(GeneralValues.HOTEL_FRAGMENT,true);
                    tinyDB.putBoolean(GeneralValues.RESTAURANT_FRAGMENT,false);
                    tinyDB.putBoolean(GeneralValues.PLACES_FRAGMENT,false);
                }else if(position==1){
                    tinyDB.putBoolean(GeneralValues.HOTEL_FRAGMENT,false);
                    tinyDB.putBoolean(GeneralValues.RESTAURANT_FRAGMENT,true);
                    tinyDB.putBoolean(GeneralValues.PLACES_FRAGMENT,false);
                }else if(position==2){
                    tinyDB.putBoolean(GeneralValues.HOTEL_FRAGMENT,false);
                    tinyDB.putBoolean(GeneralValues.RESTAURANT_FRAGMENT,false);
                    tinyDB.putBoolean(GeneralValues.PLACES_FRAGMENT,true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //on sort button click
        HomeActivity.sortIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPagerAdapter adapter = ((ViewPagerAdapter) viewPager.getAdapter());
                if(tinyDB.getBoolean(GeneralValues.HOTEL_FRAGMENT)){
                    HotelsFragment fragment = (HotelsFragment) adapter.getItem(0);
                    fragment.showHotelsSortDialog();
                }else if(tinyDB.getBoolean(GeneralValues.RESTAURANT_FRAGMENT)){
                    RestaurantsFragment fragment = (RestaurantsFragment) adapter.getItem(1);
                    fragment.showRestaurantsSortDialog();
                }else if(tinyDB.getBoolean(GeneralValues.PLACES_FRAGMENT)){
                    PlacesNearFragment fragment = (PlacesNearFragment) adapter.getItem(2);
                    fragment.showPlacesNearBySortDialog();
                }
            }
        });

        //on filter button click
        HomeActivity.filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPagerAdapter adapter = ((ViewPagerAdapter) viewPager.getAdapter());
                if(tinyDB.getBoolean(GeneralValues.RESTAURANT_FRAGMENT)){
                    RestaurantsFragment fragment = (RestaurantsFragment) adapter.getItem(1);
                    fragment.showRestaurantsFilterDialog();
                }else if(tinyDB.getBoolean(GeneralValues.PLACES_FRAGMENT)){
                    PlacesNearFragment fragment = (PlacesNearFragment) adapter.getItem(2);
                    fragment.showPlacesNearByFilterDialog();
                }
            }
        });

        return v;
    }

    //change tabs font
    public void changeTabsFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "bauhaus.ttf"), Typeface.NORMAL);
                }
            }
        }
    }

    //setting of fragments into view pager
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new HotelsFragment(), getString(R.string.hotels));
        adapter.addFragment(new RestaurantsFragment(), getString(R.string.restaurants));
        adapter.addFragment(new PlacesNearFragment(), getString(R.string.places_near));
        viewPager.setAdapter(adapter);
    }

    //add fragments
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

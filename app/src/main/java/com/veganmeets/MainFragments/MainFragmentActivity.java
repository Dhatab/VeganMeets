package com.veganmeets.MainFragments;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.veganmeets.R;

public class MainFragmentActivity extends AppCompatActivity {
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        //Setup the Viewpager with sections adapter
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(5);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Fragment_Account(), "Account");
        adapter.addFragment(new Fragment_Swipes(), "Matches");
        adapter.addFragment(new Fragment_MatchChats(), "Chat");
        viewPager.setAdapter(adapter);
    }
}

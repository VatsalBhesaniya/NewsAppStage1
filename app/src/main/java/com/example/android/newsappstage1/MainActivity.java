package com.example.android.newsappstage1;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        setupVeiwPager(mViewPager);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupVeiwPager(ViewPager veiwPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new WorldFragment(), "World");
        adapter.addFragment(new TechnologyFragment(), "Technology");
        adapter.addFragment(new FashionFragment(), "Fashion");
        adapter.addFragment(new BusinessFragment(), "Business");
        adapter.addFragment(new CultureFragment(), "Culture");
        adapter.addFragment(new SportsFragment(), "Sports");
        adapter.addFragment(new LifestyleFragment(), "Lifestyle");
        adapter.addFragment(new PoliticsFragment(), "Politics");
        veiwPager.setAdapter(adapter);
    }
}

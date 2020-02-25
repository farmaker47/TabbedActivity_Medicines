package com.george.tabbedactivity_medicines;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.george.tabbedactivity_medicines.ui.DetailsActivity;
import com.george.tabbedactivity_medicines.ui.main.SearchFragmentNavigation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityOptionsCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;

import com.george.tabbedactivity_medicines.ui.main.SectionsPagerAdapter;

public class TabbedMainActivity extends AppCompatActivity implements SearchFragmentNavigation.OnFragmentInteractionListenerSearchFragmentNavigation {

    public static final String NAME_TO_PASS = "name_to_pass";

    private SearchFragmentNavigation searchFragmentNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchFragmentNavigation = new SearchFragmentNavigation();

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), savedInstanceState);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public void onFragmentInteraction(String drugName, String string1, ImageView sharedImage) {

        //making animation above api
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(TabbedMainActivity.this, DetailsActivity.class);
            intent.putExtra(NAME_TO_PASS, drugName);
            // bundle for the transition effect
            Bundle bundle = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            this,
                            sharedImage,
                            sharedImage.getTransitionName()
                    ).toBundle();
            startActivity(intent, bundle);
        } else {
            Intent intent = new Intent(TabbedMainActivity.this, DetailsActivity.class);
            intent.putExtra(NAME_TO_PASS, drugName);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        /*searchFragmentNavigation.inCaseNotLoaded();*/
    }
}
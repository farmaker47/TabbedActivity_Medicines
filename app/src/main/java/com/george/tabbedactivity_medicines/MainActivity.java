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

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.george.tabbedactivity_medicines.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements SearchFragmentNavigation.OnFragmentInteractionListenerSearchFragmentNavigation {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onFragmentInteraction(String string, String string1, ImageView sharedImage) {

        /*Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        startActivity(intent);*/

        //making animation above api
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            // bundle for the transition effect
            Bundle bundle = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            this,
                            sharedImage,
                            sharedImage.getTransitionName()
                    ).toBundle();
            startActivity(intent, bundle);
        } else {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            startActivity(intent);
        }

    }
}
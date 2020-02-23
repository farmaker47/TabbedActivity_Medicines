package com.george.tabbedactivity_medicines.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.george.tabbedactivity_medicines.TabbedMainActivity;
import com.george.tabbedactivity_medicines.ui.main.PackageFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.Toast;

import com.george.tabbedactivity_medicines.R;

public class DetailsActivity extends AppCompatActivity implements PackageFragment.OnFragmentInteractionListenerPackage{

    private FragmentManager fragmentManager;
    private PackageFragment packageFragment;
    private String nameOfDrug = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        if (intent.hasExtra(TabbedMainActivity.NAME_TO_PASS)) {
            nameOfDrug = intent.getStringExtra(TabbedMainActivity.NAME_TO_PASS);
        }

        if(savedInstanceState == null){
            Bundle bundle = new Bundle();
            bundle.putString(TabbedMainActivity.NAME_TO_PASS, nameOfDrug);
            packageFragment = new PackageFragment();
            packageFragment.setArguments(bundle);
            fragmentManager.beginTransaction().add(R.id.containerDetailsFragments, packageFragment).commit();
        }

        //check internet connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            //TODO

        } else {
            Toast.makeText(DetailsActivity.this, R.string.please_connect_to_internet, Toast.LENGTH_SHORT).show();
        }

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
    public void onFragmentInteractionPackage(String string, String string2, String string3) {

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        packageFragment.backPressButton();
    }
}

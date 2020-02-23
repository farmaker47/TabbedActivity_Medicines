package com.george.tabbedactivity_medicines.ui.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.george.tabbedactivity_medicines.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.onomasiaProiontos};
    private final Context mContext;
    private Bundle instanceState;

    public SectionsPagerAdapter(Context context, FragmentManager fm, Bundle savedInstanceState) {
        super(fm);
        mContext = context;
        instanceState = savedInstanceState;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a NameFragment (defined as a static inner class below).

        if(position==0){
            return SearchFragmentNavigation.newInstance("ena");
        }else{
            return NameFragment.newInstance(position + 1);
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 1 total pages.
        return 1;
    }
}
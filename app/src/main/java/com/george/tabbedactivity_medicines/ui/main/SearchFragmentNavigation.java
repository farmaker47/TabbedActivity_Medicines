package com.george.tabbedactivity_medicines.ui.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.george.tabbedactivity_medicines.R;
import com.george.tabbedactivity_medicines.ui.utils.ClearableAutoComplete2;
import com.george.tabbedactivity_medicines.ui.utils.SoloupisEmptyRecyclerView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragmentNavigation.OnFragmentInteractionListenerSearchFragmentNavigation} interface
 * to handle interaction events.
 * Use the {@link SearchFragmentNavigation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragmentNavigation extends Fragment implements SearchFragmentNavigationAdapter.SearchClickItemListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ArrayList<String> hitaList;
    private ClearableAutoComplete2 editTextView;
    private View searchView;
    private SoloupisEmptyRecyclerView mRecyclerViewSearchFragment;
    private SearchFragmentNavigationAdapter mSearchFragmentNavigationAdapter;
    private LinearLayoutManager layoutManager;
    private Timer timer;
    private static final String HITS_LIST_FOR_ROTATION = "hits_list_for_rotation";
    private static final String EDIT_TEXT_STRING = "edit_text_string";
    private String afterRotationEditTextString = "";

    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";
    private Parcelable savedRecyclerLayoutState;
    private Context context;

    private static final String ATC = "atc";
    private static final String COMPANY = "company";
    private static final String DRUG = "drug";
    private static final String CITATION = "citation";
    private static final String ICD10 = "icd10";
    private static final String SUBSTANCE = "substance";
    private static final String PACKAGE = "package";
    private static final String SUPPLEMENT = "supplement";

    private ImageView imageViewSearchFragment;
    private ProgressBar progressBarSearchFragment;

    private OnFragmentInteractionListenerSearchFragmentNavigation mListener;


    public interface OnFragmentInteractionListenerSearchFragmentNavigation {
        // TODO: Update argument type and name
        void onFragmentInteraction(String string, String string1, ImageView sharedImage);
    }

    public SearchFragmentNavigation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragmentNavigation.
     */
    public static SearchFragmentNavigation newInstance(String param1, String param2) {
        SearchFragmentNavigation fragment = new SearchFragmentNavigation();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        searchView = inflater.inflate(R.layout.fragment_search_fragment_navigation, container, false);
        context = getActivity();
        //catching views
        hitaList = new ArrayList();
        editTextView = searchView.findViewById(R.id.autoSearchNavigation);

        //Upon creation we check if there is internet connection
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        imageViewSearchFragment = searchView.findViewById(R.id.imageSearchFragment);
        progressBarSearchFragment = searchView.findViewById(R.id.progressSearchFragment);

        //restore recycler view at same position
        //we get layoutmanager position, string of autocomplete, and the current list so there is no need for re-quering the API
        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            afterRotationEditTextString = savedInstanceState.getString(EDIT_TEXT_STRING);
            if (savedInstanceState.containsKey(HITS_LIST_FOR_ROTATION)) {
                hitaList = savedInstanceState.getStringArrayList(HITS_LIST_FOR_ROTATION);
            }
        }

        //timer for calculating time when user is typing
        timer = new Timer();

        mRecyclerViewSearchFragment = searchView.findViewById(R.id.recyclerViewSearchFragment);
        //setting the empty view, only with custom Recycler view
        mRecyclerViewSearchFragment.setEmptyView(imageViewSearchFragment);

        mRecyclerViewSearchFragment.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerViewSearchFragment.setLayoutManager(layoutManager);

        mSearchFragmentNavigationAdapter = new SearchFragmentNavigationAdapter(getActivity(), hitaList, this);
        mRecyclerViewSearchFragment.setAdapter(mSearchFragmentNavigationAdapter);


        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            //TODO
        } else {
            Toast.makeText(getActivity(), R.string.please_connect_to_internet, Toast.LENGTH_SHORT).show();
        }

        editTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // user is typing: reset already started timer (if existing)
                if (timer != null) {
                    timer.cancel();
                }

            }

            @Override
            public void afterTextChanged(final Editable editable) {

                ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (editTextView.length() > 2 && networkInfo != null && networkInfo.isConnected()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            //we determine if it is on creation or after rotation
                            if (savedInstanceState == null) {

                                //actions in specific row
                                hideKeyboard();

                                fetchInfo(editTextView.getText().toString().trim());

                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressBarSearchFragment.setVisibility(View.VISIBLE);
                                    }
                                });

                            } else if (savedInstanceState != null && !afterRotationEditTextString.equals(editTextView.getText().toString().trim())) {

                                hideKeyboard();

                                fetchInfo(editTextView.getText().toString().trim());

                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressBarSearchFragment.setVisibility(View.VISIBLE);
                                    }
                                });
                            }

                        }
                    }, 900);
                }
            }
        });

        //clicking X button at right inside autocomplete
        editTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= (editTextView.getRight() - editTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        editTextView.setText("");
                        clearDataOfList();
                        showKeyboard();
                        return true;
                    }else if (motionEvent.getRawX() < (editTextView.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width() - editTextView.getLeft())) {

                        //TODO
                        /*IntentIntegrator integrator = new IntentIntegrator(getActivity());
                        integrator.initiateScan();*/

                        return true;
                    }
                }
                return false;
            }
        });

        return searchView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //saving string of autocomplete, layoutmanager so the recyclerview go back at same position, and list of results
        outState.putString(EDIT_TEXT_STRING, editTextView.getText().toString().trim());
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, layoutManager.onSaveInstanceState());
        outState.putStringArrayList(HITS_LIST_FOR_ROTATION, hitaList);
    }


    private void hideKeyboard() {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(searchView.getApplicationWindowToken(), 0);
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextView, InputMethodManager.SHOW_IMPLICIT);
    }

    private void clearDataOfList() {
        hitaList = new ArrayList<>();
        mSearchFragmentNavigationAdapter.setHitsData(new ArrayList<String>());
        mSearchFragmentNavigationAdapter.notifyDataSetChanged();
    }

    private void fetchInfo(String queryString) {


        /*Call<PojoSearch> call = soloupisAPI.getAllInfo();*/

        /*call.enqueue(new Callback<PojoSearch>() {
            @Override
            public void onResponse(Call<PojoSearch> call, Response<PojoSearch> response) {
                PojoSearch pojoSearch = response.body();
                *//*Hits[] hits = pojoSearch.getHits();
                Hits oneHit = hits[2];
                String string = oneHit.getName();
                mTextMessage.setText(string);*//*

                String queryString = pojoSearch.getTotalHits();
                mTextMessage.setText(queryString);
            }

            @Override
            public void onFailure(Call<PojoSearch> call, Throwable t) {
                Toast.makeText(MainActivityBottomNavigation.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String s, String s1,ImageView sharedImage) {
        if (mListener != null) {
            mListener.onFragmentInteraction(s, s1,sharedImage);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerSearchFragmentNavigation) {
            mListener = (OnFragmentInteractionListenerSearchFragmentNavigation) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //click from adapter
    @Override
    public void onListItemClick(int itemIndex,ImageView sharedImage,String type) {


    }
}

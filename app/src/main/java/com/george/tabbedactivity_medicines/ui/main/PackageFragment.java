package com.george.tabbedactivity_medicines.ui.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;



import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.george.tabbedactivity_medicines.R;
import com.george.tabbedactivity_medicines.TabbedMainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static com.george.tabbedactivity_medicines.ui.main.SearchFragmentNavigation.URL_TO_SERVE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PackageFragment.OnFragmentInteractionListenerPackage} interface
 * to handle interaction events.
 * Use the {@link PackageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PackageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String BASE_IMAGE_URL = URL_TO_SERVE;
    private View packageView;
    private String nameFromEntry, baseName = "";
    private String stringForDeletingRow, responseStg, photoPackageCode, tokenToUse, internetInfo = "";
    private TextView toolBarText, titleTextViewGray, sigentrosiTextView, farmakMorfi, syskeuasia, numberDoses,
            administrationRoutesTextView, textViewBarcode, textViewEof, textViewHdika, textViewGge, textViewSoloupis, textViewNosok,
            textViewXondr, textViewLianiki, textViewSpcHeader;
    private LinearLayout linearSistatika, linearYpeuthinos, linearDiathesis, linearSpc;
    private ImageView detailActivityImage;
    private FloatingActionButton floatingActionButton;
    private SQLiteDatabase mdb;
    private int columnIDIndex = -1;

    private static final String ATC = "atc";
    private static final String COMPANY = "company";
    private static final String DRUG = "drug";
    private static final String CITATION = "citation";
    private static final String ICD10 = "icd10";
    private static final String SUBSTANCE = "substance";
    private static final String PACKAGE = "package";

    private boolean sharedLogged;

    ///////////////////////////////////////
    private WebView webView;
    private ProgressBar progressBar;

    private OnFragmentInteractionListenerPackage mListener;

    public interface OnFragmentInteractionListenerPackage {
        // TODO: Update argument type and name
        void onFragmentInteractionPackage(String string, String string2, String string3);
    }

    public PackageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PackageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PackageFragment newInstance(String param1, String param2) {
        PackageFragment fragment = new PackageFragment();
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
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        packageView = inflater.inflate(R.layout.activity_scrolling_details_fragment, container, false);
        context = getActivity();
        //catching the views
        /*toolBarText = packageView.findViewById(R.id.toolbarText);
        titleTextViewGray = packageView.findViewById(R.id.titleTextViewGray);
        sigentrosiTextView = packageView.findViewById(R.id.sigentrosi);
        detailActivityImage = packageView.findViewById(R.id.detail_activity_image);
        farmakMorfi = packageView.findViewById(R.id.farmakMorfi);
        syskeuasia = packageView.findViewById(R.id.syskeuasia);
        numberDoses = packageView.findViewById(R.id.numberDoses);
        administrationRoutesTextView = packageView.findViewById(R.id.administrationRoutes);
        linearSistatika = packageView.findViewById(R.id.linearSistatika);
        linearYpeuthinos = packageView.findViewById(R.id.linearYpeuthinos);
        linearDiathesis = packageView.findViewById(R.id.linearDiathesis);
        textViewBarcode = packageView.findViewById(R.id.textViewBarcode);
        textViewEof = packageView.findViewById(R.id.textViewEof);
        textViewHdika = packageView.findViewById(R.id.textViewIdika);
        textViewGge = packageView.findViewById(R.id.textViewGge);
        textViewSoloupis = packageView.findViewById(R.id.textViewSoloupis);
        textViewNosok = packageView.findViewById(R.id.textViewNosok);
        textViewXondr = packageView.findViewById(R.id.textViewXondrik);
        textViewLianiki = packageView.findViewById(R.id.textViewLianiki);
        floatingActionButton = packageView.findViewById(R.id.fab);*/

        webView = packageView.findViewById(R.id.webViewPackage);

        textViewSpcHeader = packageView.findViewById(R.id.textViewSpcHeader);
        textViewSpcHeader.setTextColor(Color.BLUE);

        linearSpc = packageView.findViewById(R.id.linearSpc);
        progressBar = packageView.findViewById(R.id.progressBarPackage);

        //Load dummy image
        detailActivityImage = packageView.findViewById(R.id.detail_activity_image);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Picasso.get().load(R.drawable.recipe_icon).into(detailActivityImage);
        }

        nameFromEntry = getArguments().getString(TabbedMainActivity.NAME_TO_PASS);
        titleTextViewGray = packageView.findViewById(R.id.titleTextViewGray);
        titleTextViewGray.setText(nameFromEntry);


        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedLogged = sharedPreferences.getBoolean(EntryScreenNavigation.LOGGED_IN, false);
        tokenToUse = sharedPreferences.getString(EntryScreenNavigation.TOKEN_STRING, "");*/

        Toolbar toolbar = packageView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Objects.requireNonNull(getActivity()).onBackPressed();

            }
        });

        /*floatingActionButton = packageView.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                progressBar.setVisibility(View.VISIBLE);


            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Log.e("PAGEFINISHED","PACKAGE");

                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        //Enable Javascript
        webView.getSettings().setJavaScriptEnabled(true);
        //Clear All and load url
        webView.loadUrl(URL_TO_SERVE);

        return packageView;
    }

    public void backPressButton(){

        webView.loadUrl("javascript:(function(){l=document.getElementById('form1:btnBack');e=document.createEvent('HTMLEvents');e.initEvent('click',true,true);l.dispatchEvent(e);})()");

        /*if (getView() == null) {
            return;
        }

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    return true;
                }
                return false;
            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String string, String string2, String string3) {
        if (mListener != null) {
            mListener.onFragmentInteractionPackage(string, string2, string3);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListenerPackage) {
            mListener = (OnFragmentInteractionListenerPackage) context;
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

}


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
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.george.tabbedactivity_medicines.R;
import com.george.tabbedactivity_medicines.ui.utils.ClearableAutoComplete2;
import com.george.tabbedactivity_medicines.ui.utils.SoloupisEmptyRecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Objects;
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

    private ArrayList hitaList;
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

    public static final String URL_TO_SERVE = "https://services.eof.gr/drugsearch/SearchName.iface";
    private static String parsedText = "";

    private WebView webView;

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
     * @return A new instance of fragment SearchFragmentNavigation.
     */
    public static SearchFragmentNavigation newInstance(String param1) {
        SearchFragmentNavigation fragment = new SearchFragmentNavigation();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
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
        webView = searchView.findViewById(R.id.webViewEof);


        //Upon creation we check if there is internet connection
        ConnectivityManager connMgr = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            //TODO
        } else {
            Toast.makeText(getActivity(), R.string.please_connect_to_internet, Toast.LENGTH_SHORT).show();
        }

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

                if (editTextView.length() >= 4 && networkInfo != null && networkInfo.isConnected()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            //we determine if it is on creation or after rotation
                            if (savedInstanceState == null) {

                                //actions in specific row


                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        hideKeyboard();
                                        fetchInfo(editTextView.getText().toString().trim());
                                        progressBarSearchFragment.setVisibility(View.VISIBLE);
                                    }
                                });

                            } else if (savedInstanceState != null && !afterRotationEditTextString.equals(editTextView.getText().toString().trim())) {


                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        hideKeyboard();
                                        fetchInfo(editTextView.getText().toString().trim());
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
                    } else if (motionEvent.getRawX() < (editTextView.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width() - editTextView.getLeft())) {

                        //TODO
                        /*IntentIntegrator integrator = new IntentIntegrator(getActivity());
                        integrator.initiateScan();*/

                        return true;
                    }
                }
                return false;
            }
        });

        //Enable Javascript
        webView.getSettings().setJavaScriptEnabled(true);
        //Clear All and load url
        webView.loadUrl("javascript:(function(){l=document.getElementById('form1:btnClear');e=document.createEvent('HTMLEvents');e.initEvent('click',true,true);l.dispatchEvent(e);})()");
        webView.loadUrl(URL_TO_SERVE);

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

        //Put value
        webView.loadUrl("javascript:(function(){l=document.getElementById('form1:txtDrname').value='" + queryString + "';})()");

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch info
        if (networkInfo != null && networkInfo.isConnected()) {

            //Click button
            webView.loadUrl("javascript:(function(){l=document.getElementById('form1:btnSubmit');e=document.createEvent('HTMLEvents');e.initEvent('click',true,true);l.dispatchEvent(e);})()");

            //Wait 2 seconds
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    webView.evaluateJavascript(
                            "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {

                                    //Make progressBar disappear
                                    progressBarSearchFragment.setVisibility(View.INVISIBLE);

                                    JsonReader reader = new JsonReader(new StringReader(html));
                                    reader.setLenient(true);
                                    try {
                                        if (reader.peek() == JsonToken.STRING) {
                                            String domStr = reader.nextString();
                                            if (domStr != null) {
                                                parseSecondColumn(domStr);

                                            }
                                        }
                                    } catch (IOException e) {
                                        // handle exception
                                    }

                                }
                            });
                }
            }, 2000);

        } else {
            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG).show();
        }

    }

    private void parseSecondColumn(String html) throws IOException {

        StringBuilder builder = new StringBuilder();
        ArrayList<String> arrayForTextView = new ArrayList<>();

        Document doc = Jsoup.parse(html);

        if (checkElement(doc.select("table[id=form1:tblResults]").first())) {

            //Select column that attribute ends in lnkDRNAME
            Elements row = doc.select("table[id=form1:tblResults]").select(".iceDatTblCol2").select("a[id$=lnkDRNAME]");

            if (row != null) {
                for (Element element : row) {
                    String text = element.text();
                    arrayForTextView.add(text);
                }

                if (arrayForTextView.size() == 0) {
                    Toast.makeText(getActivity(), R.string.no_results, Toast.LENGTH_LONG).show();
                }

                for (int i = 0; i < arrayForTextView.size(); i++) {
                    builder.append(arrayForTextView.get(i)).append("\n");
                    parsedText = builder.toString();
                }

                mSearchFragmentNavigationAdapter.setHitsData(arrayForTextView);
                //we reset position to 0
                mRecyclerViewSearchFragment.smoothScrollToPosition(0);
                layoutManager.scrollToPositionWithOffset(0, 0);

                //running the animation at the beggining of showing the list
                runLayoutAnimation(mRecyclerViewSearchFragment);
            }
        } else {
            //In case server is down for maintainance
            Toast.makeText(getActivity(), R.string.eof_error, Toast.LENGTH_LONG).show();

        }


    }

    private static boolean checkElement(Element elem) {
        return elem != null;
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String s, String s1, ImageView sharedImage) {
        if (mListener != null) {
            mListener.onFragmentInteraction(s, s1, sharedImage);
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
    public void onListItemClick(int itemIndex, ImageView sharedImage, String type) {

        webView.loadUrl("javascript:(function(){l=document.getElementById('form1:tblResults:" + itemIndex + ":lnkDRNAME');e=document.createEvent('HTMLEvents');e.initEvent('click',true,true);l.dispatchEvent(e);})()");

        mListener.onFragmentInteraction(type, type, sharedImage);
    }

    public void inCaseNotLoaded() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                webView.loadUrl("javascript:(function(){l=document.getElementById('form1:btnBack');e=document.createEvent('HTMLEvents');e.initEvent('click',true,true);l.dispatchEvent(e);})()");

            }
        }, 1000);
    }



    @Override
    public void onResume() {
        super.onResume();

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl("javascript:(function(){l=document.getElementById('form1:btnBack');e=document.createEvent('HTMLEvents');e.initEvent('click',true,true);l.dispatchEvent(e);})()");

            }
        });

        webView.loadUrl(URL_TO_SERVE);
        Log.e("OnRESUME","load");

    }
}

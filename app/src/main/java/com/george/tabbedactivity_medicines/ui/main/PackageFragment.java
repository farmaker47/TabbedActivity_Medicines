package com.george.tabbedactivity_medicines.ui.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;


import android.text.Html;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Objects;

import timber.log.Timber;

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
    private static final String URL_FOR_PDFs = "https://services.eof.gr";
    private View packageView;
    private String nameFromEntry = "";
    private String stringForDeletingRow, responseStg, photoPackageCode, tokenToUse, internetInfo = "";
    private TextView toolBarText, titleTextViewGray, sigentrosiTextView, farmakMorfi, syskeuasia, numberDoses,
            administrationRoutesTextView, textViewBarcode, textViewEof, textViewHdika, textViewGge, textViewSoloupis, textViewNosok,
            textViewXondr, textViewLianiki, nomikoKathestos, morfiEofTextView,
            periektikotitaTextView, odosXorigisisTextView, kodikosAtcTextView, perigrafiAtcTextView,
            onomasiaEtairiasTextView, addressEtairiasTextView, tilefonoEtairiasTextView, faxEtairiasTextView,
            mailEtairiasTextView, perilipsiXaraktiristikonTextView, filoOdigionTextView, ekthesiAksiologisisTextView;
    private LinearLayout linearSistatika, linearSpc;
    private ImageView detailActivityImage;
    private FloatingActionButton floatingActionButton;


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
        farmakMorfi = packageView.findViewById(R.id.farmakMorfi);
        nomikoKathestos = packageView.findViewById(R.id.nomikoKathestos);
        morfiEofTextView = packageView.findViewById(R.id.morfiEofTextView);
        periektikotitaTextView = packageView.findViewById(R.id.periektikotita);
        odosXorigisisTextView = packageView.findViewById(R.id.odosXorigisisTextView);
        kodikosAtcTextView = packageView.findViewById(R.id.kodikosAtcTextView);
        perigrafiAtcTextView = packageView.findViewById(R.id.perigrafiAtcTextView);
        onomasiaEtairiasTextView = packageView.findViewById(R.id.onomasiaEtairiasTextView);
        addressEtairiasTextView = packageView.findViewById(R.id.addressEtairiasTextView);
        tilefonoEtairiasTextView = packageView.findViewById(R.id.tilefonoEtairiasTextView);
        faxEtairiasTextView = packageView.findViewById(R.id.faxEtairiasTextView);
        mailEtairiasTextView = packageView.findViewById(R.id.mailEtairiasTextView);
        perilipsiXaraktiristikonTextView = packageView.findViewById(R.id.perilipsiXaraktiristikonTextView);
        filoOdigionTextView = packageView.findViewById(R.id.filoOdigionTextView);
        ekthesiAksiologisisTextView = packageView.findViewById(R.id.ekthesiAksiologisisTextView);

        linearSistatika = packageView.findViewById(R.id.linearSistatika);

        webView = packageView.findViewById(R.id.webViewPackage);

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

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                progressBar.setVisibility(View.VISIBLE);


            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.INVISIBLE);

                //TODO = Fetch ALL info from Webview
                fetchAllInfo();
            }
        });

        //Enable Javascript
        webView.getSettings().setJavaScriptEnabled(true);
        //Clear All and load url
        webView.loadUrl(URL_TO_SERVE);

        return packageView;
    }

    private void fetchAllInfo() {

        webView.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String html) {

                        //Make progressBar disappear

                        JsonReader reader = new JsonReader(new StringReader(html));
                        reader.setLenient(true);
                        try {
                            if (reader.peek() == JsonToken.STRING) {
                                String domStr = reader.nextString();
                                if (domStr != null) {
                                    parseAllInfo(domStr);
                                }
                            }
                        } catch (IOException e) {
                            // handle exception
                        }

                    }
                });
    }

    private void parseAllInfo(String domStr) {

        Document doc = Jsoup.parse(domStr);

        if (checkElement(doc.select("input[id=form1:btnBack]").first())) {

            /*logAll(doc.body().toString());*/

            //Kodikos EOF
            /*"<span class=\"iceOutTxt\" id=\"form1:txtDRUGID\">0232809</span>"*/
            if (checkElement(doc.select("span[id=form1:txtDRUGID]").first())) {
                Element kodikosEof = doc.select("span[id=form1:txtDRUGID]").first();
                farmakMorfi.setText(kodikosEof.text());
            }


            //Nomiko kathestos
            if (checkElement(doc.select("span[id=form1:txtLESTATUS]").first())) {
                Element nomikoKAthestos = doc.select("span[id=form1:txtLESTATUS]").first();
                nomikoKathestos.setText(nomikoKAthestos.text());
            }

            //Morfi
            if (checkElement(doc.select("span[id=form1:tblDrform:0:txtformcode]").first())) {
                Element morfi = doc.select("span[id=form1:tblDrform:0:txtformcode]").first();
                morfiEofTextView.setText(morfi.text());
            }

            //Periektikotita
            if (checkElement(doc.select("span[id=form1:tblDrform:0:txtStrength]").first())) {
                Element periektikotita = doc.select("span[id=form1:tblDrform:0:txtStrength]").first();
                periektikotitaTextView.setText(periektikotita.text());
            }

            //Odos Xorigisis
            if (checkElement(doc.select("span[id=form1:tblDRROUTE:0:txtDrroute]").first())) {
                Element odos = doc.select("span[id=form1:tblDRROUTE:0:txtDrroute]").first();
                odosXorigisisTextView.setText(odos.text());
            }

            //Kodikos ATC
            if (checkElement(doc.select("span[id=form1:tblATC:0:txtATCcode]").first())) {
                Element kodikos_atc = doc.select("span[id=form1:tblATC:0:txtATCcode]").first();
                kodikosAtcTextView.setText(kodikos_atc.text());
            }

            //Perigrafi ATC
            if (checkElement(doc.select("span[id=form1:tblATC:0:txtATCDESCR]").first())) {
                Element perigrafi_atc = doc.select("span[id=form1:tblATC:0:txtATCDESCR]").first();
                perigrafiAtcTextView.setText(perigrafi_atc.text());
            }

            //Sistatika
            if (checkElement(doc.select("table[id=form1:tblActiveIngredients]").first())) {
                ArrayList<String> arrayForTextView = new ArrayList<>();

                Elements row = doc.select("table[id=form1:tblActiveIngredients]").select(".iceDatTblCol2").select("span[id$=SUNAME]");

                if (row != null) {
                    for (Element element : row) {
                        String text = element.text();
                        arrayForTextView.add(text);
                    }
                }

                for (int i = 0; i < arrayForTextView.size(); i++) {

                    //Creating a view
                    TextView ingredient = new TextView(context);
                    ingredient.setText(arrayForTextView.get(i));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 8, 8, 8);
                    ingredient.setLayoutParams(params);
                    ingredient.setTextSize(18);
                    ingredient.setPaintFlags(ingredient.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    ingredient.setTextColor(Color.BLUE);
                    ingredient
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //TODO
                                }
                            });
                    linearSistatika.addView(ingredient);

                }

            }

            //Onomasia etairias
            if (checkElement(doc.select("td[id=form1:panelGrid6-0-1]").select("span[id=form1:txtName]").first())) {
                Element onomasia_etairias = doc.select("td[id=form1:panelGrid6-0-1]").select("span[id=form1:txtName]").first();
                onomasiaEtairiasTextView.setText(onomasia_etairias.text());
            }

            //Address
            if (checkElement(doc.select("td[id=form1:panelGrid6-2-1]").select("span[id=form1:txtAddress]").first())) {
                Element address_etairias = doc.select("td[id=form1:panelGrid6-2-1]").select("span[id=form1:txtAddress]").first();
                addressEtairiasTextView.setText(address_etairias.text());
            }

            //tilefono
            if (checkElement(doc.select("td[id=form1:panelGrid6-3-1]").select("span[id=form1:txtPhone]").first())) {
                Element tilefono_etairias = doc.select("td[id=form1:panelGrid6-3-1]").select("span[id=form1:txtPhone]").first();
                tilefonoEtairiasTextView.setText(tilefono_etairias.text());
            }

            //Fax
            if (checkElement(doc.select("td[id=form1:panelGrid6-4-1]").select("span[id=form1:txtFax]").first())) {
                Element fax_etairias = doc.select("td[id=form1:panelGrid6-4-1]").select("span[id=form1:txtFax]").first();
                faxEtairiasTextView.setText(fax_etairias.text());
            }

            //Mail
            if (checkElement(doc.select("td[id=form1:panelGrid6-5-1]").select("span[id=form1:txtEmail]").first())) {
                Element mail_etairias = doc.select("td[id=form1:panelGrid6-5-1]").select("span[id=form1:txtEmail]").first();
                mailEtairiasTextView.setText(mail_etairias.text());
            }

            //Perilipsi xaraktiristikon
/*
            "<div id=\"form1:orDrugSPC_cont\"><a class=\"iceOutLnk\" href=\"/drugsearch/block/resource/MTk4OTkyOTkxMA==/SPC_0232802_1.pdf\" id=\"form1:orDrugSPC\" target=\"_blank\">SPC_0232802_1.pdf</a></div>"
*/
            if (checkElement(doc.select("div[id=form1:orDrugSPC_cont]").select(".iceOutLnk").first())) {
                Element perilipsi = doc.select("div[id=form1:orDrugSPC_cont]").select(".iceOutLnk").first();
                perilipsiXaraktiristikonTextView.setText(perilipsi.text());

                final Element perilipsiPdf = doc.select("div[id=form1:orDrugSPC_cont]").select("a[href]").first();

                Timber.e(URL_FOR_PDFs + perilipsiPdf.attr("href"));

                perilipsiXaraktiristikonTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myWebLink = new Intent(Intent.ACTION_VIEW);
                        myWebLink.setData(Uri.parse(URL_FOR_PDFs + perilipsiPdf.attr("href")));
                        if (myWebLink.resolveActivity(context.getPackageManager()) != null) {
                            startActivity(myWebLink);
                        }
                    }
                });
            }


            //Filo Odigion
            if (checkElement(doc.select("div[id=form1:orDrugPL_cont]").select(".iceOutLnk").first())) {
                Element filoOdigion = doc.select("div[id=form1:orDrugPL_cont]").select(".iceOutLnk").first();
                filoOdigionTextView.setText(filoOdigion.text());

                final Element filoOdigionPdf = doc.select("div[id=form1:orDrugPL_cont]").select("a[href]").first();

                Timber.e(URL_FOR_PDFs + filoOdigionPdf.attr("href"));

                filoOdigionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myWebLink = new Intent(Intent.ACTION_VIEW);
                        myWebLink.setData(Uri.parse(URL_FOR_PDFs + filoOdigionPdf.attr("href")));
                        if (myWebLink.resolveActivity(context.getPackageManager()) != null) {
                            startActivity(myWebLink);
                        }
                    }
                });
            }

            //ekthesi aksiologisis
/*
            "<td class=\"icePnlGrdCol2\" id=\"form1:grdPAR-0-1\"><a class=\"iceCmdLnk\" href=\"javascript:;\" id=\"form1:lnkPARMrp\" onblur=\"setFocus('');\" onclick=\"window.open('http://mri.medagencies.org/Human/Product/FullTextSearch?includeProductDetails=true&amp;includeProductDetails=true&amp;includeSPCResults=true&amp;includeSPCResults=true&amp;includePARResults=true&amp;includePARResults=true&amp;includeFPLResults=true&amp;includeFPLResults=true&amp;includeFLBResults=true&amp;includeFLBResults=true&amp;includeFPIResults=true&amp;includeFPIResults=true&amp;searchTerm=REMERON 30MG/TAB'); return false;var form=formOf(this);form['form1:_idcl'].value='form1:lnkPARMrp';return iceSubmit(form,this,event);\" onfocus=\"setFocus(this.id);\">Προβολή (H.M.A.)</a></td>"
*/
            if (checkElement(doc.select("td[id=form1:grdPAR-0-1]").select(".iceCmdLnk").first())) {
                Element ekthesiAksiologisis = doc.select("td[id=form1:grdPAR-0-1]").select(".iceCmdLnk").first();
                ekthesiAksiologisisTextView.setText(ekthesiAksiologisis.text());
            }

        }

    }

    private void logAll(String html) {
        int maxLogSize = 1000;
        for (int i = 0; i <= html.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > html.length() ? html.length() : end;
            Log.e("YES_EXIST_ALL", html.substring(start, end));
        }
    }

    private static boolean checkElement(Element elem) {
        return elem != null;
    }

    public void backPressButton() {

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

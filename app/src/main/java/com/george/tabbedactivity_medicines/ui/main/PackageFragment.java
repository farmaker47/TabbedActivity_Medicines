package com.george.tabbedactivity_medicines.ui.main;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ahmadrosid.svgloader.SvgLoader;
import com.george.tabbedactivity_medicines.R;
import com.george.tabbedactivity_medicines.TabbedMainActivity;
import com.george.tabbedactivity_medicines.databinding.ActivityScrollingDetailsFragmentBinding;
import com.george.tabbedactivity_medicines.ui.DetailsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import timber.log.Timber;

import static android.content.Context.DOWNLOAD_SERVICE;
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
    private TextView titleTextViewGray, farmakMorfi, nomikoKathestos, morfiEofTextView,
            periektikotitaTextView, odosXorigisisTextView, kodikosAtcTextView, perigrafiAtcTextView,
            onomasiaEtairiasTextView, addressEtairiasTextView, tilefonoEtairiasTextView, faxEtairiasTextView,
            mailEtairiasTextView, perilipsiXaraktiristikonTextView, filoOdigionTextView, ekthesiAksiologisisTextView, ingredient, ingredient2;
    private LinearLayout linearSistatika, linearSpc;
    private ImageView detailActivityImage;
    private FloatingActionButton floatingActionButton;
    private HashMap<String, String> cookies;
    private String cookieStringStripped;


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

    private ActivityScrollingDetailsFragmentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /*packageView = inflater.inflate(R.layout.activity_scrolling_details_fragment, container, false);*/
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_scrolling_details_fragment, container, false);

        context = getActivity();

        farmakMorfi = binding.farmakMorfi;
        /*farmakMorfi = packageView.findViewById(R.id.farmakMorfi);*/
        nomikoKathestos = binding.nomikoKathestos;
        /*nomikoKathestos = packageView.findViewById(R.id.nomikoKathestos);*/
        morfiEofTextView = binding.morfiEofTextView;
        /*morfiEofTextView = packageView.findViewById(R.id.morfiEofTextView);*/
        periektikotitaTextView = binding.periektikotita;
        /*periektikotitaTextView = packageView.findViewById(R.id.periektikotita);*/
        odosXorigisisTextView = binding.odosXorigisisTextView;
        /*odosXorigisisTextView = packageView.findViewById(R.id.odosXorigisisTextView);*/
        kodikosAtcTextView = binding.kodikosAtcTextView;
        /*kodikosAtcTextView = packageView.findViewById(R.id.kodikosAtcTextView);*/
        perigrafiAtcTextView = binding.perigrafiAtcTextView;
        /*perigrafiAtcTextView = packageView.findViewById(R.id.perigrafiAtcTextView);*/
        onomasiaEtairiasTextView = binding.onomasiaEtairiasTextView;
        /*onomasiaEtairiasTextView = packageView.findViewById(R.id.onomasiaEtairiasTextView);*/
        addressEtairiasTextView = binding.addressEtairiasTextView;
        /*addressEtairiasTextView = packageView.findViewById(R.id.addressEtairiasTextView);*/
        tilefonoEtairiasTextView = binding.tilefonoEtairiasTextView;
        /*tilefonoEtairiasTextView = packageView.findViewById(R.id.tilefonoEtairiasTextView);*/
        faxEtairiasTextView = binding.faxEtairiasTextView;
        /*faxEtairiasTextView = packageView.findViewById(R.id.faxEtairiasTextView);*/
        mailEtairiasTextView = binding.mailEtairiasTextView;
        /*mailEtairiasTextView = packageView.findViewById(R.id.mailEtairiasTextView);*/
        perilipsiXaraktiristikonTextView = binding.perilipsiXaraktiristikonTextView;
        /*perilipsiXaraktiristikonTextView = packageView.findViewById(R.id.perilipsiXaraktiristikonTextView);*/
        filoOdigionTextView = binding.filoOdigionTextView;
        /*filoOdigionTextView = packageView.findViewById(R.id.filoOdigionTextView);*/
        ekthesiAksiologisisTextView = binding.ekthesiAksiologisisTextView;
        /*ekthesiAksiologisisTextView = packageView.findViewById(R.id.ekthesiAksiologisisTextView);*/

        linearSistatika = binding.linearSistatika;
        /*linearSistatika = packageView.findViewById(R.id.linearSistatika);*/

        webView = binding.webViewPackage;
        /*webView = packageView.findViewById(R.id.webViewPackage);*/

        progressBar = binding.progressBarPackage;
        /*progressBar = packageView.findViewById(R.id.progressBarPackage);*/

        //Load dummy image
        detailActivityImage = packageView.findViewById(R.id.detail_activity_image);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Picasso.get().load(R.drawable.recipe_icon).into(detailActivityImage);
        }

        nameFromEntry = Objects.requireNonNull(getArguments()).getString(TabbedMainActivity.NAME_TO_PASS);

        titleTextViewGray = binding.titleTextViewGray;
        /*titleTextViewGray = packageView.findViewById(R.id.titleTextViewGray);*/
        titleTextViewGray.setText(nameFromEntry);


        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedLogged = sharedPreferences.getBoolean(EntryScreenNavigation.LOGGED_IN, false);
        tokenToUse = sharedPreferences.getString(EntryScreenNavigation.TOKEN_STRING, "");*/

        Toolbar toolbar = binding.toolbar;
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
                progressBar.setVisibility(View.GONE);

                fetchAllInfo();

                cookieStringStripped = CookieManager.getInstance().getCookie(url);

            }
        });

        //Enable Javascript
        webView.getSettings().setJavaScriptEnabled(true);
        //Clear All and load url
        webView.loadUrl(URL_TO_SERVE);

        //Upon click on downloadable link outcomment below
        /*webView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(url));


                request.setMimeType(mimeType);


                String cookies = CookieManager.getInstance().getCookie(url);


                request.addRequestHeader("cookie", cookies);


                request.addRequestHeader("User-Agent", userAgent);


                request.setDescription("Downloading file...");


                request.setTitle("Spc file");


                request.allowScanningByMediaScanner();


                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);

            }
        });*/

        return binding.getRoot();
    }

    private void fetchAllInfo() {

        webView.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String html) {

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

            //Kodikos EOF
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
                final ArrayList<String> arrayForTextView = new ArrayList<>();

                Elements row = doc.select("table[id=form1:tblActiveIngredients]").select(".iceDatTblCol2").select("span[id$=SUNAME]");

                if (row != null) {
                    for (Element element : row) {
                        String text = element.text();
                        arrayForTextView.add(text);
                    }
                }

                /*//If there is no text finish activity
                if (arrayForTextView.size() == 0) {
                    Objects.requireNonNull(getActivity()).finish();

                    Toast.makeText(context, R.string.try_again, Toast.LENGTH_LONG).show();

                }*/

                for (int i = 0; i < arrayForTextView.size(); i++) {

                    /*//Creating a view
                    TextView ingredient = new TextView(context);
                    ingredient.setText(arrayForTextView.get(i));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(8, 8, 8, 8);
                    ingredient.setLayoutParams(params);
                    ingredient.setTextSize(18);
                    ingredient.setPaintFlags(ingredient.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    ingredient.setTextColor(Color.BLUE);
                    //ripple effect
                    TypedValue outValue = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    ingredient.setBackgroundResource(outValue.resourceId);

                    ingredient
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //TODO
                                }
                            });
                    linearSistatika.addView(ingredient);*/

                    //Creating a view
                    LinearLayout linearLayout = new LinearLayout(context);
                    LinearLayout.LayoutParams paramsLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    paramsLinear.setMargins(0, 0, 0, 24);
                    linearLayout.setLayoutParams(paramsLinear);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    //text for description
                    final TextView ingredient2 = new TextView(context);
                    ingredient2.setText(R.string.wait_String);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params2.setMargins(32, 8, 8, 16);
                    ingredient2.setLayoutParams(params2);
                    ingredient2.setTextSize(16);
                    ingredient2.setTextColor(Color.GRAY);
                    ingredient2.setVisibility(View.GONE);
                    /*final String resultString2 = pingAndFetchText(DRUGS_CA, arrayForTextView.get(i));*/

                    //text for name
                    TextView ingredient = new TextView(context);
                    ingredient.setText(arrayForTextView.get(i));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(32, 8, 8, 16);
                    ingredient.setLayoutParams(params);
                    ingredient.setTextSize(18);
                    ingredient.setPaintFlags(ingredient.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    ingredient.setTextColor(Color.BLUE);
                    //ripple effect
                    TypedValue outValue = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    ingredient.setBackgroundResource(outValue.resourceId);
                    final int finalI = i;
                    ingredient
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mListener.onFragmentInteractionPackage(arrayForTextView.get(finalI), arrayForTextView.get(finalI), arrayForTextView.get(finalI));
                                }
                            });

                    linearLayout.addView(ingredient);
                    linearLayout.addView(ingredient2);

                    linearSistatika.addView(linearLayout);

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
            if (checkElement(doc.select("div[id=form1:orDrugSPC_cont]").select(".iceOutLnk").first())) {
                final Element perilipsi = doc.select("div[id=form1:orDrugSPC_cont]").select(".iceOutLnk").first();
                perilipsiXaraktiristikonTextView.setText(perilipsi.text());

                final Element perilipsiPdf = doc.select("div[id=form1:orDrugSPC_cont]").select("a[href]").first();

                Timber.e(URL_FOR_PDFs + perilipsiPdf.attr("href"));

                perilipsiXaraktiristikonTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

/*
                        webView.loadUrl("javascript:(function(){l=document.getElementById('form1:orDrugSPC_cont');e=document.createEvent('HTMLEvents');e.initEvent('click',true,true);l.dispatchEvent(e);})()");
*/

                        /*Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(URL_FOR_PDFs + perilipsiPdf.attr("href"))); // only used based on your example.
                        String title = "Select a browser";
                        // Create intent to show the chooser dialog
                        Intent chooser = Intent.createChooser(intent, title);
                        // Verify the original intent will resolve to at least one activity
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            startActivity(chooser);
                        }*/

                        /*webView.setWebViewClient(new WebViewClient() {
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                String url2 = "https://services.eof.gr/";
                                // all links  with in ur site will be open inside the webview
                                //links that start ur domain example(http://www.example.com/)
                                if (url != null && url.startsWith(url2)) {
                                    return false;
                                }
                                // all links that points outside the site will be open in a normal android browser
                                else {
                                    view.getContext().startActivity(
                                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                    return true;
                                }
                            }
                        });
                        webView.loadUrl("https://services.eof.gr/drugsearch/SearchName.iface");

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                String urlString = URL_FOR_PDFs + perilipsiPdf.attr("href");
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setPackage("com.android.chrome");
                                if (intent.resolveActivity(context.getPackageManager()) != null) {
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(context, R.string.installChrome, Toast.LENGTH_LONG).show();
                                }
                            }
                        }, 2000);*/

/*Intent myWebLink = new Intent(Intent.ACTION_VIEW);
                        myWebLink.setData(Uri.parse(URL_FOR_PDFs + perilipsiPdf.attr("href")));
                        if (myWebLink.resolveActivity(context.getPackageManager()) != null) {
                            startActivity(myWebLink);
                        }*/

                        if (perilipsi.text().endsWith(".pdf") || perilipsi.text().endsWith(".doc") || perilipsi.text().endsWith(".docx")) {

                            ((DetailsActivity) Objects.requireNonNull(getActivity())).setNameOfPdf(perilipsi.text());
                            ((DetailsActivity) Objects.requireNonNull(getActivity())).beginDownload(URL_FOR_PDFs + perilipsiPdf.attr("href"),
                                    cookieStringStripped, perilipsi.text());

                            progressBar.setVisibility(View.VISIBLE);

                        } /*else if (perilipsi.text().equals("Προβολή (H.M.A.)") || perilipsi.text().equals("Προβολή (E.M.A.)")) {


                        }*/ else {
                            Toast.makeText(context, "Not a valid file to download. Please try another one.", Toast.LENGTH_LONG).show();
                        }


                    }
                });
            } else if (checkElement(doc.select("td[id=form1:grdSPCLink-0-0]").select(".iceCmdLnk").first())) {

                final Element perilipsi = doc.select("td[id=form1:grdSPCLink-0-0]").select(".iceCmdLnk").first();
                perilipsiXaraktiristikonTextView.setText(perilipsi.text());

                /*final Element perilipsiPdf = doc.select("div[id=form1:orDrugSPC_cont]").select("a[href]").first();*/

                /*Timber.e(URL_FOR_PDFs + perilipsiPdf.attr("href"));*/
                perilipsiXaraktiristikonTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String findString = perilipsi.attr("onclick");
                        int iend = findString.indexOf(";");

                        /*Toast.makeText(context, perilipsi.attr("onclick").substring(13, iend), Toast.LENGTH_LONG).show();*/

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(perilipsi.attr("onclick").substring(13, iend)));
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
            }


            //Filo Odigion
            if (checkElement(doc.select("div[id=form1:orDrugPL_cont]").select(".iceOutLnk").first())) {
                final Element filoOdigion = doc.select("div[id=form1:orDrugPL_cont]").select(".iceOutLnk").first();
                filoOdigionTextView.setText(filoOdigion.text());

                final Element filoOdigionPdf = doc.select("div[id=form1:orDrugPL_cont]").select("a[href]").first();

                Timber.e(URL_FOR_PDFs + filoOdigionPdf.attr("href"));

                filoOdigionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Intent myWebLink = new Intent(Intent.ACTION_VIEW);
                        myWebLink.setData(Uri.parse(URL_FOR_PDFs + filoOdigionPdf.attr("href")));
                        if (myWebLink.resolveActivity(context.getPackageManager()) != null) {
                            startActivity(myWebLink);
                        }*/
                        /*((DetailsActivity) Objects.requireNonNull(getActivity())).viewPdf();*/
                        if (filoOdigion.text().endsWith(".pdf") || filoOdigion.text().endsWith(".doc") || filoOdigion.text().endsWith(".docx")) {
                            ((DetailsActivity) Objects.requireNonNull(getActivity())).setNameOfPdf(filoOdigion.text());
                            ((DetailsActivity) Objects.requireNonNull(getActivity())).beginDownload(URL_FOR_PDFs + filoOdigionPdf.attr("href"),
                                    cookieStringStripped, filoOdigion.text());

                            progressBar.setVisibility(View.VISIBLE);
                        } /*else if (filoOdigion.text().equals("Προβολή (H.M.A.)") || filoOdigion.text().equals("Προβολή (E.M.A.)")) {


                        }*/ else {
                            Toast.makeText(context, "Not a valid file to download. Please try another one.", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            } else if (checkElement(doc.select("td[id=form1:grdPLLink-0-0]").select(".iceCmdLnk").first())) {

                final Element filoOdigion = doc.select("td[id=form1:grdPLLink-0-0]").select(".iceCmdLnk").first();
                filoOdigionTextView.setText(filoOdigion.text());

                filoOdigionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String findString = filoOdigion.attr("onclick");
                        int iend = findString.indexOf(";");

                        /*Toast.makeText(context, filoOdigion.attr("onclick").substring(13, iend), Toast.LENGTH_LONG).show();*/

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(filoOdigion.attr("onclick").substring(13, iend)));
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            startActivity(intent);
                        }

                    }
                });
            }

            //ekthesi aksiologisis
            if (checkElement(doc.select("td[id=form1:grdPAR-0-1]").select(".iceCmdLnk").first())) {
                final Element ekthesiAksiologisis = doc.select("td[id=form1:grdPAR-0-1]").select(".iceCmdLnk").first();
                ekthesiAksiologisisTextView.setText(ekthesiAksiologisis.text());

                ekthesiAksiologisisTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ekthesiAksiologisis.text().endsWith(".pdf") || ekthesiAksiologisis.text().endsWith(".doc") || ekthesiAksiologisis.text().endsWith(".docx")) {
                            ((DetailsActivity) Objects.requireNonNull(getActivity())).setNameOfPdf(ekthesiAksiologisis.text());
                            ((DetailsActivity) Objects.requireNonNull(getActivity())).beginDownload(URL_FOR_PDFs + ekthesiAksiologisis.attr("href"),
                                    cookieStringStripped, ekthesiAksiologisis.text());

                            progressBar.setVisibility(View.VISIBLE);
                        } else if (ekthesiAksiologisis.text().equals("Προβολή (H.M.A.)") || ekthesiAksiologisis.text().equals("Προβολή (E.M.A.)")) {

                            String findString = ekthesiAksiologisis.attr("onclick");
                            int iend = findString.indexOf(";");

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(ekthesiAksiologisis.attr("onclick").substring(13, iend)));
                            if (intent.resolveActivity(context.getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(context, "Not a valid file to download ekthesi. Please try another one.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

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

    public void makeProgressBarInVisible() {
        progressBar.setVisibility(View.GONE);
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

        /*//Jsoup
        new Thread(new Runnable() {
            @Override
            public void run() {
                cookies = new HashMap<>();
                try {
                    Connection.Response loginFormResponse = Jsoup.connect(URL_TO_SERVE)
                            .method(Connection.Method.GET)
                            .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                            .execute();

                    cookies.putAll(loginFormResponse.cookies());
                    Log.e("FRAGMENT_JSOUP", new Gson().toJson(cookies));


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/


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

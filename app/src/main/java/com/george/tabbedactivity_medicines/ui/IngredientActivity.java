package com.george.tabbedactivity_medicines.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmadrosid.svgloader.SvgLoader;
import com.george.tabbedactivity_medicines.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class IngredientActivity extends AppCompatActivity {

    private String ingredientName = "";

    private static final String DRUGS_CA = "https://www.drugbank.ca/drugs";
    private StringBuilder builderImage, builderInfo;
    ArrayList<String> arrayForChoiceUrl, arrayForChoiceText;
    private String parsedText, parsedInfo;
    private boolean isPresent = true;
    private TextView textDrastiki;
    private LinearLayout linearDrastiki;
    private ExpandableTextView textViewResults;
    private ImageView imageMeds;
    private LinearLayout linearChoice;
    private static final String TAG = "DrastikiImage";
    private String drastikiGeneral;

    public static boolean isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        linearDrastiki = findViewById(R.id.linearDrastiki);
        textDrastiki = findViewById(R.id.textDrastiki);
        textViewResults = findViewById(R.id.expand_text_view);
        imageMeds = findViewById(R.id.imageMeds);
        linearChoice = findViewById(R.id.linearChoice);

        Intent intent = getIntent();
        if (intent.hasExtra(DetailsActivity.NAME_OF_INGREDIENT)) {
            ingredientName = intent.getStringExtra(DetailsActivity.NAME_OF_INGREDIENT);
        }

        /*Log.e("NAME", ingredientName);*/
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Active Ingredient");

        pingAndGet(DRUGS_CA);

    }

    //Fetch structure and description
    private void pingAndGet(final String url) {

        arrayForChoiceText = new ArrayList<>();
        arrayForChoiceUrl = new ArrayList<>();
        builderImage = new StringBuilder();
        builderInfo = new StringBuilder();
        textDrastiki.setText("");
        textViewResults.setText("");

        new Thread(new Runnable() {
            @Override
            public void run() {

                HashMap<String, String> cookies = new HashMap<>();
                try {
                    if (url.equals("https://www.drugbank.ca/drugs")) {
                        Log.e("INPUT", "input");
                        Connection.Response loginFormResponse = Jsoup.connect(DRUGS_CA)
                                .method(Connection.Method.GET)
                                .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                                .execute();

                        cookies.putAll(loginFormResponse.cookies());
                        Log.e(TAG, cookies.toString());
                        //find the form
                        FormElement loginForm = (FormElement) loginFormResponse.parse()
                                .select(".form-inline").first();

                        //fill info in element
                        if (loginForm != null) {
                            Element loginField = loginForm.select(".search-query").first();
                            Log.e(TAG, loginField.toString());
                            if (loginField != null) {
                                loginField.val(ingredientName);
                            }

                            //execute
                            Connection.Response loginActionResponse = loginForm.submit()
                                    .data(".search-query", ".search-query")
                                    .cookies(loginFormResponse.cookies())
                                    .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                                    .execute();


                            ArrayList<String> arrayForTextView = new ArrayList<>();

                            builderImage.append("https://www.drugbank.ca");

                            Document doc = loginActionResponse.parse();
                            Log.e("INPUT", doc.toString());
                            Log.e("INPUT", loginActionResponse.toString());
                            //check if table exists
                            if (checkElement(doc.select("a[class=moldbi-vector-thumbnail]").first())) {
                                Log.e("INPUT", "image");
                                Elements imageUrl = doc.select("a[class=moldbi-vector-thumbnail]");
                                for (Element element : imageUrl) {
                                    parsedText = element.attr("href");
                                }
                                builderImage.append(parsedText);

                                //text
                                Elements tag = doc.getElementsByTag("p");

                                for (Element element : tag) {
                                    Log.e("Escitalopram", element.text());
                                    String text = element.text();
                                    arrayForTextView.add(text);
                                }

                                if (arrayForTextView.size() == 0) {
                                    builderInfo.append("No text to display");
                                } else if (arrayForTextView.size() == 1) {
                                    builderInfo.append(arrayForTextView.get(0));
                                } else if (arrayForTextView.size() == 2) {
                                    builderInfo.append(arrayForTextView.get(0)).append("\n\n").append(arrayForTextView.get(1));
                                } else if (arrayForTextView.size() == 3) {
                                    builderInfo.append(arrayForTextView.get(0)).append("\n\n").append(arrayForTextView.get(1)).append("\n\n").
                                            append(arrayForTextView.get(2));
                                } else if (arrayForTextView.size() == 4) {
                                    builderInfo.append(arrayForTextView.get(0)).append("\n\n").append(arrayForTextView.get(1)).append("\n\n").
                                            append(arrayForTextView.get(2)).append("\n\n").append(arrayForTextView.get(3));
                                } else {
                                    builderInfo.append(arrayForTextView.get(0)).append("\n\n").append(arrayForTextView.get(1)).append("\n\n").
                                            append(arrayForTextView.get(2)).append("\n\n").append(arrayForTextView.get(3));
                                }

                                parsedInfo = builderInfo.toString();

                            } else if (checkElement(doc.select("div[class=unearth-search-hit my-1]").select("h2[class=hit-link]").first())) {

                                isPresent = false;

                                Elements names = doc.select("div[class=unearth-search-hit my-1]").select("h2[class=hit-link]");

                                for (Element element : names) {
                                    String text = element.text();
                                    arrayForChoiceText.add(text);

                                    Elements aElem = element.getElementsByTag("a");
                                    for (Element small : aElem) {
                                        String link = small.attr("href");
                                        arrayForChoiceUrl.add(link);
                                    }


                                }


                            } else if (!checkElement(doc.select("div[class=unearth-search-hit my-1]").select("h2[class=hit-link]").first())) {

                                isPresent = true;

                            }

                            Elements rows = doc.select("dd[class=col-md-10 col-sm-8]");


                        }
                    } else {

                        Connection.Response loginFormResponseTrial = Jsoup.connect(url)
                                .method(Connection.Method.GET)
                                .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                                .execute();
                        ArrayList<String> arrayForTextView = new ArrayList<>();

                        builderImage.append("https://www.drugbank.ca");

                        Document doc = loginFormResponseTrial.parse();
                        //check if table exists
                        if (checkElement(doc.select("a[class=moldbi-vector-thumbnail]").first())) {

                            Elements imageUrl = doc.select("a[class=moldbi-vector-thumbnail]");
                            for (Element element : imageUrl) {
                                parsedText = element.attr("href");
                            }
                            builderImage.append(parsedText);
                            //text
                            Elements tag = doc.getElementsByTag("p");

                            for (Element element : tag) {
                                String text = element.text();
                                arrayForTextView.add(text);
                            }

                            if (arrayForTextView.size() == 0) {
                                builderInfo.append("No text to display");
                            } else if (arrayForTextView.size() == 1) {
                                builderInfo.append(arrayForTextView.get(0));
                            } else if (arrayForTextView.size() == 2) {
                                builderInfo.append(arrayForTextView.get(0)).append("\n\n").append(arrayForTextView.get(1));
                            } else if (arrayForTextView.size() == 3) {
                                builderInfo.append(arrayForTextView.get(0)).append("\n\n").append(arrayForTextView.get(1)).append("\n\n").
                                        append(arrayForTextView.get(2));
                            } else if (arrayForTextView.size() == 4) {
                                builderInfo.append(arrayForTextView.get(0)).append("\n\n").append(arrayForTextView.get(1)).append("\n\n").
                                        append(arrayForTextView.get(2)).append("\n\n").append(arrayForTextView.get(3));
                            } else {
                                builderInfo.append(arrayForTextView.get(0)).append("\n\n").append(arrayForTextView.get(1)).append("\n\n").
                                        append(arrayForTextView.get(2)).append("\n\n").append(arrayForTextView.get(3));
                            }

                            parsedInfo = builderInfo.toString();

                        } else if (checkElement(doc.select("div[class=unearth-search-hit my-1]").select("h2[class=hit-link]").first())) {

                            isPresent = false;

                            Elements names = doc.select("div[class=unearth-search-hit my-1]").select("h2[class=hit-link]");

                            for (Element element : names) {
                                String text = element.text();
                                arrayForChoiceText.add(text);

                                Elements aElem = element.getElementsByTag("a");
                                for (Element small : aElem) {
                                    String link = small.attr("href");
                                    arrayForChoiceUrl.add(link);
                                }


                            }


                        } else if (!checkElement(doc.select("div[class=unearth-search-hit my-1]").select("h2[class=hit-link]").first())) {

                            isPresent = true;

                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!builderImage.toString().equals("https://www.drugbank.ca")) {
                            SvgLoader.pluck()
                                    .with(IngredientActivity.this)
                                    .setPlaceHolder(R.drawable.recipe_icon, R.drawable.recipe_icon)
                                    .load(builderImage.toString(), imageMeds);

                            textViewResults.setText(parsedInfo);
                            textDrastiki.setText(drastikiGeneral);
                            linearDrastiki.setVisibility(View.VISIBLE);

                        } else if (builderImage.toString().equals("https://www.drugbank.ca") && isPresent) {
                            Picasso.get().load(R.drawable.recipe_icon).into(imageMeds);
                            textViewResults.setText(getString(R.string.drastikiNoResults));
                            Log.i("LATHOS1", builderImage.toString());

                            linearDrastiki.setVisibility(View.VISIBLE);

                        } else if (builderImage.toString().equals("https://www.drugbank.ca") && !isPresent) {
                            Picasso.get().load(R.drawable.recipe_icon).into(imageMeds);
                            textDrastiki.setText(drastikiGeneral);
                            Log.i("LATHOS2", builderImage.toString());
                            textViewResults.setText("No results. Please try again!");

                            for (int i = 0; i < arrayForChoiceUrl.size(); i++) {
                                final String name = arrayForChoiceText.get(i);
                                final String urlText = arrayForChoiceUrl.get(i);

                                //Creating a view
                                TextView ingredient = new TextView(IngredientActivity.this);
                                ingredient.setText(name);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                params.setMargins(8, 8, 8, 8);
                                ingredient.setLayoutParams(params);
                                ingredient.setTextSize(18);
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                                    ingredient.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                }
                                ingredient.setPaintFlags(ingredient.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                ingredient.setTextColor(Color.BLUE);
                                ingredient
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Toast.makeText(IngredientActivity.this, builderImage.toString() + url, Toast.LENGTH_SHORT).show();
                                                drastikiGeneral = name;
                                                pingAndGet(builderImage.toString() + urlText);
                                                linearChoice.removeAllViews();

                                            }
                                        });

                                linearChoice.addView(ingredient);
                            }

                            linearDrastiki.setVisibility(View.VISIBLE);

                        }

                    }
                });
            }
        }).start();
    }

    private static boolean checkElement(Element elem) {
        return elem != null;
    }
}

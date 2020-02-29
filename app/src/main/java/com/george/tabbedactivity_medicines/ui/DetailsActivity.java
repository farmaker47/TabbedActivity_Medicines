package com.george.tabbedactivity_medicines.ui;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.george.tabbedactivity_medicines.BuildConfig;
import com.george.tabbedactivity_medicines.TabbedMainActivity;
import com.george.tabbedactivity_medicines.ui.download.EofService;
import com.george.tabbedactivity_medicines.ui.download.FileDownloader;
import com.george.tabbedactivity_medicines.ui.main.PackageFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.george.tabbedactivity_medicines.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import timber.log.Timber;

public class DetailsActivity extends AppCompatActivity implements PackageFragment.OnFragmentInteractionListenerPackage {

    private FragmentManager fragmentManager;
    private PackageFragment packageFragment;
    private String nameOfDrug = "";

    private static final String TAG = "DetailsActivity";
    private static final String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE};

    public static final String URL_EOF_SERVICE = "eof_url_service";
    public static final String NAME_EOF_SERVICE = "name_url_service";

    private long downloadID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        fragmentManager = getSupportFragmentManager();

        Intent intent = getIntent();
        if (intent.hasExtra(TabbedMainActivity.NAME_TO_PASS)) {
            nameOfDrug = intent.getStringExtra(TabbedMainActivity.NAME_TO_PASS);
        }

        if (savedInstanceState == null) {
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

        ActivityCompat.requestPermissions(this, PERMISSIONS, 112);

        //Register receiver
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                packageFragment.makeProgressBarInVisible();
                viewPdf();
            }
        }
    };

    @Override
    public void onFragmentInteractionPackage(String string, String string2, String string3) {

    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        packageFragment.backPressButton();
    }

    //Check permissions
    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void viewPdf() {
        Timber.v("view() Method invoked ");

        if (!hasPermissions(this, PERMISSIONS)) {

            Timber.v("download() Method DOESN'T HAVE PERMISSIONS");

            Toast.makeText(getApplicationContext(), "You don't have read access !", Toast.LENGTH_LONG).show();

        } else {

            File pdfFile = new File(getExternalFilesDir(null), "spcrecipe.pdf");

            /*File d = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);  // -> filename = maven.pdf
            File pdfFile = new File(d, stringPdf);*/

            Timber.v("view() Method pdfFile " + pdfFile.getAbsolutePath());

            Uri path = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", pdfFile);


            Log.v(TAG, "view() Method path " + path);

            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
            }
        }
        Timber.v("view() Method completed ");

    }

    public void downloadPdf(String downloadUrl, String name) {
        Log.v(TAG, "download() Method invoked ");

        if (!hasPermissions(this, PERMISSIONS)) {

            Log.v(TAG, "download() Method DON'T HAVE PERMISSIONS ");

            Toast.makeText(getApplicationContext(), "You don't have write access !", Toast.LENGTH_LONG).show();

        } else {
            Log.v(TAG, "download() Method HAVE PERMISSIONS ");

            //new DownloadFile().execute("http://maven.apache.org/maven-1.x/maven.pdf", "maven.pdf");
            new DownloadFile().execute(downloadUrl, name);

        }

        Log.v(TAG, "download() Method completed ");

    }

    public class DownloadFile extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {


            String fileUrl = strings[0];
            Log.e("URL", fileUrl);
            String fileName = strings[1];  // -> spc.pdf
            /*String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            File pdfFile = new File(folder, fileName);
            Log.e(TAG, "doInBackground() pdfFile invoked " + pdfFile.getAbsolutePath());
            Log.e(TAG, "doInBackground() pdfFile invoked " + pdfFile.getAbsoluteFile());

            try {
                pdfFile.createNewFile();
                Log.e(TAG, "doInBackground() file created" + pdfFile);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground() error" + e.getMessage());
                Log.e(TAG, "doInBackground() error" + e.getStackTrace());


            }*/
            FileDownloader.downloadFromInternet(DetailsActivity.this, fileUrl, fileName);
            Log.e("DOWNLOADFROMINTERNET", fileUrl);

            return fileName;
        }

        @Override
        protected void onPostExecute(String fileNamePdf) {

            /*viewPdf(fileNamePdf);*/
            Log.e("POSTeXECUTE", "EXECUTE");


        }
    }

    public void startServiceEof(String url, String name) {

        Intent intent = new Intent(this, EofService.class);
        intent.putExtra(URL_EOF_SERVICE, url);
        intent.putExtra(NAME_EOF_SERVICE, name);
        startService(intent);
    }

    public void beginDownload(String url, String cookiesBrowser) {
        File file = new File(getExternalFilesDir(null), "spcrecipe.pdf");
        if (file.exists()) {
            file.delete();
        }

        Log.e("CookiesDetails", cookiesBrowser);


        /*
        Create a DownloadManager.Request with all the information necessary to start the download
         */
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle("SPC File")// Title of the Download Notification
                .setDescription("Downloading")// Description of the Download Notification
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true);
        request.addRequestHeader("cookie", cookiesBrowser);
        request.addRequestHeader("User-Agent", cookiesBrowser);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.

    }
}
